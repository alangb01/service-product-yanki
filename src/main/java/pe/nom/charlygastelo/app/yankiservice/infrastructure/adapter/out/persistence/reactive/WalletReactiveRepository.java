package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.document.WalletDocument;
import reactor.core.publisher.Mono;

public interface WalletReactiveRepository extends ReactiveMongoRepository<WalletDocument, String> {

    Mono<WalletDocument> findByPhone(String phone);

    Mono<WalletDocument> findByImei(String imei);
}