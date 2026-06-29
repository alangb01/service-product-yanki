package pe.nom.charlygastelo.app.yankiservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransaction;

public interface WalletEventProducerPort {

    Completable publishWalletCreated(Wallet wallet);

    Completable publishWalletUpdated(Wallet wallet);

    Completable publishWalletDeleted(Wallet wallet);

    Completable publishWalletLinkedDebitCard(Wallet wallet);

    Completable publishYankiPaymentCompleted(YankiTransaction transaction);

    Completable publishYankiPaymentFailed(YankiTransaction transaction, String reason);
}