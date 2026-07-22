package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletLinkEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletLinkRepositoryPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlinkDebitCardUseCase {

    private final WalletLinkRepositoryPort walletLinkRepository;
    private final WalletLinkEventProducerPort walletLinkEventProducer;

    public Single<WalletLink> execute(String walletId) {
        log.info("Unlinking debit card from wallet. walletId={}", walletId);

        return walletLinkRepository.findByWalletId(walletId)
                .switchIfEmpty(Single.error(
                        new WalletNotFoundException("Wallet not found: " + walletId)
                ))
                .map(WalletLink::unlinkDebitCard)
                .flatMap(walletLinkRepository::save)
                .flatMap(saved ->
                        walletLinkEventProducer.publishWalletUnlinkedDebitCard()
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Debit card unlinked successfully. walletId={}", saved.getWalletId()))
                .doOnError(error ->
                        log.error("Error unlinking debit card. walletId={}, reason={}",
                                walletId, error.getMessage(), error));
    }
}