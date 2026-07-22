package pe.nom.charlygastelo.app.yankiservice.application.usecase.transaction;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletPaymentCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Transaction;

@Component
public class PaymentUseCase {
    public Completable execute(WalletPaymentCommand cmd) {
        return Completable.complete();
    }
}
