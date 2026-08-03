package pe.nom.charlygastelo.app.yankiservice.domain.port.event;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletSendCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletValidated;

public interface WalletPaymentEventProducerPort {
    Completable publishWalletPaymentOccurred(
            WalletValidated source,
            WalletValidated target,
            WalletSendCommand cmd,
            String correlationId
        );

}