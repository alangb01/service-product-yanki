package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.LinkResult;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletLinkEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletLinkRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlinkDebitCardUseCase {

    private final WalletRepositoryPort walletRepository;
    private final WalletLinkRepositoryPort walletLinkRepository;
    private final WalletLinkEventProducerPort walletLinkEventProducer;

    public Single<LinkResult> execute(String walletId) {
        log.info("Unlinking debit card from wallet. walletId={}", walletId);

        return  walletRepository.findById(walletId)
                .switchIfEmpty(Single.error(
                        new WalletNotFoundException("Wallet not found: " + walletId)
                ))
                .flatMap(wallet->walletLinkRepository.delete(wallet.id())
                        .andThen(Single.just(wallet))
                )
                .flatMap(wallet->walletLinkEventProducer.publishWalletUnlinkedDebitCard(wallet)
                        .andThen(Single.just(new LinkResult(null,wallet)))
                )
                .doOnSuccess(result ->
                        log.info("Debit card unlinked successfully. walletId={}", walletId))
                .doOnError(error ->
                        log.error("Error unlinking debit card. walletId={}, reason={}",
                                walletId, error.getMessage(), error));
    }
}