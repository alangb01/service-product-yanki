package pe.nom.charlygastelo.app.yankiservice.domain.port.event;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;


public interface WalletLinkEventProducerPort {
    Completable publishWalletLinkedDebitCard(WalletLink walletLink, Wallet wallet);
    Completable publishWalletUnlinkedDebitCard();
}