package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class UnlinkDebitCardUseCase {

    private final WalletRepositoryPort repository;
    private final WalletEventProducerPort producer;

    public Single<Wallet> execute(String walletId) {
        log.info("Unlinking debit card from wallet. walletId={}", walletId);

        return repository.findById(walletId)
                .switchIfEmpty(Single.error(
                        new WalletNotFoundException("Wallet not found: " + walletId)
                ))
                .map(Wallet::unlinkDebitCard)
                .flatMap(repository::save)
                .flatMap(saved ->
                        producer.publishWalletUpdated(saved)
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Debit card unlinked successfully. walletId={}", saved.id()))
                .doOnError(error ->
                        log.error("Error unlinking debit card. walletId={}, reason={}",
                                walletId, error.getMessage(), error));
    }
}