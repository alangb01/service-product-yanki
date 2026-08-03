package pe.nom.charlygastelo.app.yankiservice.application.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Maybe;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Single;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardResponseEvent;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.ServiceTimeoutException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DebitCard;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.DebitCardRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.DebitCardRequestProducerPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.event.DebitCardKafkaConsumer;
import pe.nom.charlygastelo.app.yankiservice.support.RequestResponseRegistry;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebitCardRequestResponseOrchestrator implements DebitCardRepositoryPort {

    private static final long TIMEOUT_SECONDS = 2L;

    private final DebitCardRequestProducerPort producer;
    private final DebitCardKafkaConsumer consumer;

    private final RequestResponseRegistry<DebitCard> registry = new RequestResponseRegistry<>();

    @PostConstruct
    public void init() {
        consumer.registerHandler(this::handleResponse);
    }

    @Override
    public Maybe<DebitCard> findById(String debitCardId) {
        return Maybe.defer(() -> {
            String correlationId = UUID.randomUUID().toString();

            Maybe<DebitCard> response = registry.register(correlationId);

            return producer.publishDebitCardRequest(correlationId, debitCardId)
                    .andThen(response)
                    .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .onErrorResumeNext(error -> {
                        if (error instanceof java.util.concurrent.TimeoutException) {
                            return Maybe.error(new ServiceTimeoutException(
                                    "Timeout requesting debit card: " + debitCardId
                            ));
                        }
                        return Maybe.error(error);
                    });
        });
    }

    private void handleResponse(DebitCardResponseEvent event) {
        if (event.getError() != null) {
            registry.fail(event.getCorrelationId().toString(), new RuntimeException(event.getError().toString()));
            return;
        }

        DebitCard debitCard = new DebitCard(
                event.getDebitCard().getId().toString(),
                null,
                event.getDebitCard().getType().toString(),
                event.getDebitCard().getStatus().toString()
        );
        registry.complete(event.getCorrelationId().toString(), debitCard);
    }
}
