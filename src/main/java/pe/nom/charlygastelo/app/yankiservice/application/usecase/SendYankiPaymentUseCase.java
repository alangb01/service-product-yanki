package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.InsufficientWalletBalanceException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.InvalidYankiPaymentException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletInactiveException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransaction;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransactionType;
import pe.nom.charlygastelo.app.yankiservice.domain.port.MovementEventPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class SendYankiPaymentUseCase {

    private final WalletRepositoryPort repository;
    private final MovementEventPort movementEventPort;
    private final WalletEventProducerPort producer;

    public Completable execute(
            String sourcePhone,
            String targetPhone,
            BigDecimal amount,
            String description) {

        log.info("Starting Yanki payment. sourcePhone={}, targetPhone={}, amount={}",
                sourcePhone, targetPhone, amount);

        return validateRequest(sourcePhone, targetPhone, amount)
                .andThen(
                        Single.zip(
                                repository.findByPhone(sourcePhone)
                                        .switchIfEmpty(Single.error(
                                                new WalletNotFoundException(
                                                        "Source wallet not found: " + sourcePhone
                                                )
                                        )),
                                repository.findByPhone(targetPhone)
                                        .switchIfEmpty(Single.error(
                                                new WalletNotFoundException(
                                                        "Target wallet not found: " + targetPhone
                                                )
                                        )),
                                WalletPair::new
                        )
                )
                .flatMapCompletable(pair -> processPayment(pair.source(), pair.target(), amount, description))
                .doOnComplete(() ->
                        log.info("Yanki payment completed. sourcePhone={}, targetPhone={}, amount={}",
                                sourcePhone, targetPhone, amount))
                .doOnError(error ->
                        log.error("Yanki payment failed. sourcePhone={}, targetPhone={}, reason={}",
                                sourcePhone, targetPhone, error.getMessage(), error));
    }

    private Completable validateRequest(
            String sourcePhone,
            String targetPhone,
            BigDecimal amount) {

        if (sourcePhone == null || sourcePhone.isBlank()) {
            return Completable.error(
                    new InvalidYankiPaymentException("Source phone is required")
            );
        }

        if (targetPhone == null || targetPhone.isBlank()) {
            return Completable.error(
                    new InvalidYankiPaymentException("Target phone is required")
            );
        }

        if (sourcePhone.equals(targetPhone)) {
            return Completable.error(
                    new InvalidYankiPaymentException("Source and target phones cannot be the same")
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Completable.error(
                    new InvalidYankiPaymentException("Amount must be greater than zero")
            );
        }

        return Completable.complete();
    }

    private Completable processPayment(
            Wallet source,
            Wallet target,
            BigDecimal amount,
            String description) {

        if (!source.isActive()) {
            return Completable.error(
                    new WalletInactiveException("Source wallet is inactive")
            );
        }

        if (!target.isActive()) {
            return Completable.error(
                    new WalletInactiveException("Target wallet is inactive")
            );
        }

        if (source.balance().compareTo(amount) < 0) {
            return Completable.error(
                    new InsufficientWalletBalanceException("Insufficient wallet balance")
            );
        }

        Wallet debitedSource =
                source.withBalance(source.balance().subtract(amount));

        Wallet creditedTarget =
                target.withBalance(target.balance().add(amount));

        YankiTransaction transaction =
                new YankiTransaction(
                        UUID.randomUUID().toString(),
                        source.id(),
                        target.id(),
                        source.phone(),
                        target.phone(),
                        YankiTransactionType.SEND_PAYMENT,
                        amount,
                        description == null ? "" : description,
                        LocalDateTime.now()
                );

        return repository.save(debitedSource)
                .flatMap(savedSource ->
                        repository.save(creditedTarget)
                                .map(savedTarget ->
                                        new PaymentResult(
                                                savedSource,
                                                savedTarget,
                                                transaction
                                        )
                                )
                )
                .flatMapCompletable(result ->
                        movementEventPort.registerMovement(
                                        result.transaction(),
                                        result.source(),
                                        "YANKI_PAYMENT_SENT"
                                )
                                .andThen(
                                        movementEventPort.registerMovement(
                                                result.transaction(),
                                                result.target(),
                                                "YANKI_PAYMENT_RECEIVED"
                                        )
                                )
                                .andThen(
                                        producer.publishYankiPaymentCompleted(
                                                result.transaction()
                                        )
                                )
                )
                .onErrorResumeNext(error ->
                        producer.publishYankiPaymentFailed(
                                        transaction,
                                        error.getMessage()
                                )
                                .andThen(Completable.error(error))
                );
    }

    private record WalletPair(
            Wallet source,
            Wallet target
    ) {
    }

    private record PaymentResult(
            Wallet source,
            Wallet target,
            YankiTransaction transaction
    ) {
    }
}