package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.repository;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletLinkRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.mapper.WalletLinkPersistenceMapper;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.reactive.WalletLinkReactiveRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalletLinkAdapter implements WalletLinkRepositoryPort {

    private final WalletLinkReactiveRepository repository;
    private final WalletLinkPersistenceMapper mapper;

    @Override
    public Single<WalletLink> save(WalletLink link) {
        log.info("[YANKI] Linking debit card {} to wallet {}", link.getDebitCardId(), link.getWalletId());
        return Single.fromPublisher(
                repository.save(mapper.toDocument(link)).map(mapper::toDomain)
            );
    }

    @Override
    public Maybe<WalletLink> findByWalletId(String walletId) {
        log.info("[YANKI] Finding link for wallet {}", walletId);
        return Maybe.fromPublisher(repository.findByWalletId(walletId).map(mapper::toDomain));
    }

    @Override
    public Completable delete(String id) {
        log.info("[YANKI] Deleting wallet link id={}", id);
        return Completable.fromPublisher(repository.deleteById(id));
    }
}
