package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateWalletUseCase {

    private final WalletRepositoryPort repository;
    private final WalletEventProducerPort producer;

    public Single<Wallet> execute(String id, Wallet wallet) {
        log.info("Updating wallet. walletId={}", id);

        return repository.findById(id)
                .switchIfEmpty(Single.error(
                        new WalletNotFoundException("Wallet not found: " + id)
                ))
                .flatMap(existing -> repository.save(wallet))
                .flatMap(saved ->
                        producer.publishWalletUpdated(saved)
                                .doOnComplete(() ->
                                        log.info("WalletUpdatedEvent published. walletId={}", saved.id()))
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Wallet updated successfully. walletId={}", saved.id()))
                .doOnError(error ->
                        log.error("Error updating wallet. walletId={}, reason={}",
                                id, error.getMessage(), error));
    }
}