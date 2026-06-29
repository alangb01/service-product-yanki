package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.InvalidDebitCardException;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Card;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.CardEventPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class LinkDebitCardUseCase {

    private final WalletRepositoryPort repository;
    private final CardEventPort cardEventPort;
    private final WalletEventProducerPort producer;

    public Single<Wallet> execute(String walletId, String cardId) {
        log.info("Linking debit card to wallet. walletId={}, cardId={}", walletId, cardId);

        return repository.findById(walletId)
                .switchIfEmpty(Single.error(
                        new WalletNotFoundException("Wallet not found: " + walletId)
                ))
                .flatMap(wallet ->
                        cardEventPort.getById(cardId)
                                .flatMap(card -> validateDebitCard(card)
                                        .andThen(Single.just(wallet.linkDebitCard(card.id()))))
                )
                .flatMap(repository::save)
                .flatMap(saved ->
                        producer.publishWalletLinkedDebitCard(saved)
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Debit card linked successfully. walletId={}, cardId={}",
                                saved.id(), saved.debitCardId()))
                .doOnError(error ->
                        log.error("Error linking debit card. walletId={}, cardId={}, reason={}",
                                walletId, cardId, error.getMessage(), error));
    }

    private io.reactivex.rxjava3.core.Completable validateDebitCard(Card card) {
        if (!card.isDebit()) {
            return io.reactivex.rxjava3.core.Completable.error(
                    new InvalidDebitCardException("Card is not a debit card")
            );
        }

        if (!card.isActive()) {
            return io.reactivex.rxjava3.core.Completable.error(
                    new InvalidDebitCardException("Card is inactive")
            );
        }

        return io.reactivex.rxjava3.core.Completable.complete();
    }
}