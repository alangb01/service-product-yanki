package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletAlreadyExistsException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class CreateWalletUseCase {

    private final WalletRepositoryPort repository;
    private final WalletEventProducerPort producer;

    public Single<Wallet> execute(Wallet wallet) {
        log.info("Starting wallet creation. phone={}, documentNumber={}",
                wallet.phone(), wallet.documentNumber());

        return validateUniquePhone(wallet.phone())
                .andThen(validateUniqueImei(wallet.imei()))
                .andThen(repository.save(wallet))
                .flatMap(saved ->
                        producer.publishWalletCreated(saved)
                                .doOnComplete(() ->
                                        log.info("WalletCreatedEvent published. walletId={}", saved.id()))
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Wallet created successfully. walletId={}, phone={}",
                                saved.id(), saved.phone()))
                .doOnError(error ->
                        log.error("Error creating wallet. phone={}, reason={}",
                                wallet.phone(), error.getMessage(), error));
    }

    private io.reactivex.rxjava3.core.Completable validateUniquePhone(String phone) {
        return repository.findByPhone(phone)
                .flatMapCompletable(existing ->
                        io.reactivex.rxjava3.core.Completable.error(
                                new WalletAlreadyExistsException(
                                        "Wallet already exists with phone: " + phone
                                )
                        )
                );
    }

    private io.reactivex.rxjava3.core.Completable validateUniqueImei(String imei) {
        return repository.findByImei(imei)
                .flatMapCompletable(existing ->
                        io.reactivex.rxjava3.core.Completable.error(
                                new WalletAlreadyExistsException(
                                        "Wallet already exists with imei: " + imei
                                )
                        )
                );
    }
}