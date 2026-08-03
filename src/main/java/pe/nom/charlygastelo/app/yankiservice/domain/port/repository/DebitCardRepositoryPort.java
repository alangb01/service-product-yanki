package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DebitCard;

public interface DebitCardRepositoryPort {
    Maybe<DebitCard> findById(String debitCardId);
}
