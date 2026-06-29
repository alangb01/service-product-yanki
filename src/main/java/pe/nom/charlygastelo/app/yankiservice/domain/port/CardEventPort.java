package pe.nom.charlygastelo.app.yankiservice.domain.port;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Card;

public interface CardEventPort {

    Single<Card> getById(String cardId);
}