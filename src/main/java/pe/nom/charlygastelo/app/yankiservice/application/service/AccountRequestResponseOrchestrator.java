package pe.nom.charlygastelo.app.yankiservice.application.service;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.ServiceTimeoutException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Account;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.AccountRequestProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.AccountRepositoryPort;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRequestResponseOrchestrator implements AccountRepositoryPort {

    private static final long TIMEOUT_SECONDS = 5L;

    private final AccountRequestProducerPort accountProducer;

    private final Map<String, SingleEmitter<Account>> pendingRequests =
            new ConcurrentHashMap<>();

    @Override
    public Single<Account> getById(String accountId) {
        return Single.defer(() -> {
            String correlationId = UUID.randomUUID().toString();

            Single<Account> response = register(correlationId);

            return accountProducer.publishAccountRequest(correlationId, accountId)
                    .andThen(response)
                    .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .onErrorResumeNext(error -> {
                        if (error instanceof java.util.concurrent.TimeoutException) {
                            return Single.error(
                                    new ServiceTimeoutException(
                                            "Timeout requesting account: " + accountId
                                    )
                            );
                        }
                        return Single.error(error);
                    })
                    .doFinally(() -> remove(correlationId));
        });
    }

    private Single<Account> register(String correlationId) {
        return Single.create(emitter -> {
            SingleEmitter<Account> existing =
                    pendingRequests.putIfAbsent(correlationId, emitter);

            if (existing != null) {
                emitter.onError(
                        new IllegalStateException(
                                "Correlation ID already registered: " + correlationId
                        )
                );
                return;
            }

            emitter.setCancellable(() -> remove(correlationId));
        });
    }

    public void complete(String correlationId, Account account) {
        SingleEmitter<Account> emitter =
                pendingRequests.remove(correlationId);

        if (emitter == null) {
            log.warn("[ACCOUNT-RESPONSE] CorrelationId not registered: {}", correlationId);
            return;
        }

        if (!emitter.isDisposed()) {
            emitter.onSuccess(account);
        }
    }

    public void fail(String correlationId, Throwable error) {
        SingleEmitter<Account> emitter =
                pendingRequests.remove(correlationId);

        if (emitter == null) {
            log.warn("[ACCOUNT-RESPONSE] CorrelationId not registered: {}", correlationId);
            return;
        }

        if (!emitter.isDisposed()) {
            emitter.onError(error);
        }
    }

    private void remove(String correlationId) {
        pendingRequests.remove(correlationId);
    }
}
