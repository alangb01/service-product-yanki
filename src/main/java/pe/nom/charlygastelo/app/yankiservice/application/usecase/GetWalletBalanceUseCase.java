package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletLinkNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletBalance;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetWalletBalanceUseCase {

    private final WalletRepositoryPort walletRepository;
    private final WalletLinkRepositoryPort walletLinkRepository;

    public Single<WalletBalance> execute(String walletId) {

        log.info("[YANKI] Getting wallet balance. walletId={}", walletId);

        return walletRepository.findById(walletId)
            .switchIfEmpty(Single.error(
                new WalletNotFoundException("Wallet not found: " + walletId)
            ))
            .flatMap(wallet ->
                walletLinkRepository.findByWalletId(wallet.id())
                    .switchIfEmpty(Single.error(
                        new WalletLinkNotFoundException("Wallet link not found: " + wallet.id())
                    ))
                    .flatMap(link ->
                        Single.just(new WalletBalance(null,null,link, wallet))
                    )
            );
    }
}
