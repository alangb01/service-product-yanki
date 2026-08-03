package pe.nom.charlygastelo.app.yankiservice.domain.port.event;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;

public interface WalletEventProducerPort {
    Completable publishWalletCreated(Wallet wallet);
    Completable publishWalletUpdated(Wallet wallet);
    Completable publishWalletDeleted(Wallet wallet);

}