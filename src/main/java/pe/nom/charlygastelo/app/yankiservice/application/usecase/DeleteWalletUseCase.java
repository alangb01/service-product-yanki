package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteWalletUseCase {

    private final WalletRepositoryPort repository;
    private final WalletEventProducerPort producer;

    public Completable execute(String id) {
        log.info("Deleting wallet. walletId={}", id);

        return repository.findById(id)
                .switchIfEmpty(Maybe.error(
                        new WalletNotFoundException("Wallet not found: " + id)
                ))
                .toSingle()
                .flatMapCompletable(wallet ->
                        repository.deleteById(id)
                                .andThen(
                                        producer.publishWalletDeleted(wallet)
                                                .doOnComplete(() ->
                                                        log.info("WalletDeletedEvent published. walletId={}", id))
                                )
                )
                .doOnComplete(() ->
                        log.info("Wallet deleted successfully. walletId={}", id))
                .doOnError(error ->
                        log.error("Error deleting wallet. walletId={}, reason={}",
                                id, error.getMessage(), error));
    }
}