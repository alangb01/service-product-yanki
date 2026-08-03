package pe.nom.charlygastelo.app.yankiservice.application.usecase.transaction;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletPaymentResponseEvent;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletSendCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.BusinessException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletLinkNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.*;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletPaymentEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletLinkRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.support.RequestResponseRegistry;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendPaymentUseCase {

    private final WalletRepositoryPort walletRepository;
    private final WalletLinkRepositoryPort walletLinkRepository;
    private final WalletPaymentEventProducerPort paymentProducer;
    private final RequestResponseRegistry<WalletPaymentResponseEvent> registry;

//    public Single<Transaction> execute(WalletPaymentCommand cmd) {
//        return validateWallet(cmd.sourceNumber())
//                .flatMap(sourceWalletValidated->validateWallet(cmd.targetNumber())
//                        .flatMap(targetWalletValidated->
//                                sendPaymentEvent(sourceWalletValidated, targetWalletValidated, cmd))
//                );
//    }

    public Single<Transaction> execute(WalletSendCommand cmd) {
        log.info("send payment");
        Single<WalletValidated> sourceValidated=getWalletById(cmd.walletSourceId())
                .flatMap(sourceWalletValidated->validateWalletLink(sourceWalletValidated.id())
                        .flatMap(sourceWalletLink -> Single.just(new WalletValidated(sourceWalletValidated,sourceWalletLink)))
                );

        Single<WalletValidated> targetValidated=getWalletByPhone(cmd.targetNumber())
                .flatMap(walletValidated->validateWalletLink(walletValidated.id())
                        .flatMap(walletLink -> Single.just(new WalletValidated(walletValidated,walletLink)))
                );


        return sourceValidated
                .flatMap(source->targetValidated
                    .flatMap(target->sendPaymentEvent(source, target, cmd))
                );

//                walletRepository.findById(cmd.walletSourceId())
//                .switchIfEmpty(Single.error(new WalletNotFoundException("wallet source not found")))
//                .flatMap(wallet->validateWalletLink(wallet)
//                        .flatMap(targetWalletValidated->
//                                sendPaymentEvent(sourceWalletValidated, targetWalletValidated, cmd))
//                );
//            .flatMap(sourceWalletValidated->validateWallet(cmd.targetNumber())
//                .flatMap(targetWalletValidated->
//                    sendPaymentEvent(sourceWalletValidated, targetWalletValidated, cmd))
//            );
    }

    public Single<Wallet> getWalletById(String walletId) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Single.error(new WalletNotFoundException("wallet not found "+walletId)))
                .doOnSuccess(wallet->log.debug("wallet found "+wallet.id()));
    }

    public Single<Wallet> getWalletByPhone(String walletPhone){
        return walletRepository.findByPhone(walletPhone)
                .switchIfEmpty(Single.error(new WalletNotFoundException("wallet not found "+walletPhone)))
                .doOnSuccess(wallet->log.debug("wallet found "+wallet.id()+" "+walletPhone));
    }

    public Single<WalletLink> validateWalletLink(String walletId) {
        return walletLinkRepository.findByWalletId(walletId)
                .switchIfEmpty(Single.error(new WalletLinkNotFoundException("wallet has no linked debit card "+walletId)))
                .doOnSuccess(link->log.debug("LINK found "+link.getId()));
    }

    private Single<Transaction> sendPaymentEvent(
            WalletValidated source,
            WalletValidated target,
            WalletSendCommand cmd
        ) {

        String correlationId = UUID.randomUUID().toString();


        log.info("[YANKI] Sending WalletPaymentEvent corrId={} amount={}",
                correlationId, cmd.amount());

        return paymentProducer.publishWalletPaymentOccurred(
                source, target, cmd, correlationId
            )
            .andThen(registry.register(correlationId))
            .switchIfEmpty(Single.error(new RuntimeException("payment timeout")))
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
                                response.getDescription().toString()
                        );
                        return Single.just(tx);

                    default:
                        return Single.error(new RuntimeException("unknown status"));
                }
            });
    }
}
