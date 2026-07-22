package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DebitCard;

public interface DebitCardRepositoryPort {
    Single<DebitCard> getById(String debitCardId);
}
