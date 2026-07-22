package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;


import io.reactivex.rxjava3.core.Completable;

public interface AccountRequestProducerPort {
    Completable publishAccountRequest(String correlationId, String accountId);
}