package pe.nom.charlygastelo.app.yankiservice.domain.port.event;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletPaymentCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletValidationResult;

public interface WalletPaymentEventProducerPort {
    Completable publishWalletPaymentOccurred(
            WalletValidationResult source,
            WalletValidationResult target,
            WalletPaymentCommand cmd,
            String correlationId
        );

}