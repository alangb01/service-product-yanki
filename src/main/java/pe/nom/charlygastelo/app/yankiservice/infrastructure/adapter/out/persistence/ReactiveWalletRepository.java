package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ReactiveWalletRepository
        extends ReactiveMongoRepository<WalletDocument, String> {

    Mono<WalletDocument> findByPhone(String phone);

    Mono<WalletDocument> findByImei(String imei);
}