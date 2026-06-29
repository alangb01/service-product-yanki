package pe.nom.charlygastelo.app.yankiservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransaction;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;

public interface MovementEventPort {

    Completable registerMovement(
            YankiTransaction transaction,
            Wallet wallet,
            String movementType
    );
}