package pe.nom.charlygastelo.app.yankiservice.application.usecase.transaction;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletPaymentOccurredEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletPaymentResponseEvent;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletPaymentCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.BusinessException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletLinkNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.*;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletPaymentEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.DebitCardRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletLinkRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.support.RequestResponseRegistry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentUseCase {

    private final WalletRepositoryPort walletRepository;
    private final WalletLinkRepositoryPort walletLinkRepository;
    private final WalletPaymentEventProducerPort paymentProducer;
    private final RequestResponseRegistry<WalletPaymentResponseEvent> registry;


    public Completable execute(WalletPaymentCommand cmd) {
        return validateWallet(cmd.sourceNumber())
            .flatMap(sourceWalletValidated->validateWallet(cmd.targetNumber())
                    .flatMap(targetWalletValidated->
                            sendPaymentEvent(sourceWalletValidated, targetWalletValidated, cmd))
            ).ignoreElement();
    }

    public Single<WalletValidationResult> validateWallet(String phone) {
        return walletRepository.findByPhone(phone)
            .switchIfEmpty(Single.error(new WalletNotFoundException("wallet not found "+phone)))
            .doOnSuccess(wallet->log.debug("wallet found "+wallet.id()))
            .flatMap(wallet ->
                walletLinkRepository.findByWalletId(wallet.id())
                    .switchIfEmpty(Single.error(new WalletLinkNotFoundException("wallet has no linked debit card "+phone)))
                    .doOnSuccess(link->log.debug("wallet link found "+link.getId()))
                    .map(link ->new WalletValidationResult(wallet, link))
            );
    }

    private Single<Transaction> sendPaymentEvent(
            WalletValidationResult source,
            WalletValidationResult target,
            WalletPaymentCommand cmd) {

        String correlationId = UUID.randomUUID().toString();


        log.info("[YANKI] Sending WalletPaymentEvent corrId={} amount={}",
                correlationId, cmd.amount());

        return paymentProducer.publishWalletPaymentOccurred(
                    source, target, cmd, correlationId
                )
                .andThen(registry.register(correlationId))
                .flatMap(response -> {

                    switch (response.getStatus().toString()) {

                        case "FAILED":
                            return Single.error(new BusinessException(response.getMessage().toString()));

                        case "TIMEOUT":
                            return Single.error(new RuntimeException("payment timeout"));

                        case "SUCCESS":
                            Transaction tx = new Transaction(
                                    response.getTransactionId().toString(),
                                    response.getSourceWalletId().toString(),
                                    response.getTargetWalletId().toString(),
                                    TransactionType.YANKI_PAYMENT,
                                    new BigDecimal(response.getAmount()),
                                    BigDecimal.ZERO,
                                    response.getMessage().toString()
                            );
                            return Single.just(tx);

                        default:
                            return Single.error(new RuntimeException("unknown status"));
                    }
                });
    }
}
