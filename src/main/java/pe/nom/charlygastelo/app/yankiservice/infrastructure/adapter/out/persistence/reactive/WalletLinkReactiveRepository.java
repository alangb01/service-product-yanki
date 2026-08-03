package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.reactive;

import org.mapstruct.Mapper;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.document.WalletLinkDocument;
import reactor.core.publisher.Mono;

public interface WalletLinkReactiveRepository extends ReactiveMongoRepository<WalletLinkDocument, String> {
    Mono<WalletLinkDocument> findByWalletId(String walletId);
}
