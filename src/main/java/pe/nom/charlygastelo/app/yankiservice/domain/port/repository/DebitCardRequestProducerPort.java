package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;

import io.reactivex.rxjava3.core.Completable;

public interface DebitCardRequestProducerPort {
    Completable publishDebitCardRequest(String correlationId, String debitCardId);
}
