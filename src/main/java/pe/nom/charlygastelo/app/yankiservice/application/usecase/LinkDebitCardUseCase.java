package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletLinkCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.*;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DebitCard;
import pe.nom.charlygastelo.app.yankiservice.domain.model.LinkResult;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletLinkEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.DebitCardRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletLinkRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

import java.util.AbstractMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkDebitCardUseCase {

    private final WalletRepositoryPort walletRepository;
    private final WalletLinkRepositoryPort walletLinkRepository;
    private final WalletLinkEventProducerPort walletLinkedEventProducer;
    private final DebitCardRepositoryPort debitCardEventRequest;
    private final WalletLinkRepositoryPort walletLinkRepositoryPort;

    public Single<LinkResult> execute(WalletLinkCommand cmd) {
        log.info("Linking debit card to wallet={} debitCard={}",
                cmd.walletId(), cmd.debitCardId());

        return walletRepository.findById(cmd.walletId())
            .switchIfEmpty(Single.error(
                new WalletNotFoundException("Wallet not found: " + cmd.walletId())
            ))
            .flatMap(wallet ->
                    walletLinkRepository.findByWalletId(wallet.id())
                            .count()
                            .map(count -> new AbstractMap.SimpleEntry<>(wallet, count))
            )
            .flatMap(entry -> {
                Wallet wallet = entry.getKey();
                long linkCount = entry.getValue();

                if (linkCount > 0) {
                    return Single.error(new WalletLinkAlreadyExistException("Wallet already linked"));
                }
                return debitCardEventRequest.findById(cmd.debitCardId())
                        .switchIfEmpty(Single.error(
                                new DebitCardNotFoundException("Debit card not found: " + cmd.walletId())
                        ))
                        .flatMap(debitCard ->
                                validateDebitCard(debitCard)
                                        .andThen(Single.just(createLink(debitCard, wallet)))
                                        .flatMap(link ->
                                                walletLinkRepository.save(link)
                                                        .map(saved -> new LinkResult(saved, wallet)) // ← contexto interno
                                        )
                        );
            })
            .flatMap(ctx ->
                walletLinkedEventProducer.publishWalletLinkedDebitCard(
                    ctx.link(),   // WalletLink
                    ctx.wallet()   // Wallet
                ).andThen(Single.just(ctx))
            );
    }


    private WalletLink createLink(DebitCard debitCard, Wallet wallet) {
        return WalletLink.builder()
                .walletId(wallet.id())
                .debitCardId(debitCard.id())
                .build();
    }

    private Completable validateDebitCard(DebitCard card) {
        if (!card.isDebit()) {
            return Completable.error(
                    new BusinessException("Card is not a debit card")
            );
        }

        if (!card.isActive()) {
            return Completable.error(
                    new BusinessException("Card is inactive")
            );
        }

        return Completable.complete();
    }
}