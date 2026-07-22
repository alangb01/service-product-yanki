package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;

public interface WalletLinkReactiveRepository extends ReactiveMongoRepository<WalletLink, String> {

}
