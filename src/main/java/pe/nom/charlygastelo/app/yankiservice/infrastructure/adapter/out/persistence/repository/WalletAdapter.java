package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.repository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.mapper.WalletPersistentMapper;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.reactive.WalletReactiveRepository;

@RequiredArgsConstructor
@Slf4j
public class WalletAdapter implements WalletRepositoryPort {

    private final WalletReactiveRepository repository;
    private final WalletPersistentMapper mapper;

    @Override
    public Single<Wallet> save(Wallet wallet) {
        log.debug("Saving wallet: {}", wallet);
        return Single.fromPublisher(
                repository.save(mapper.toDocument(wallet))
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Wallet> findById(String id) {
        log.debug("Finding wallet by id: {}", id);
        return Maybe.fromPublisher(
                repository.findById(id)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Wallet> findByPhone(String phone) {
        log.debug("Finding wallet by phone: {}", phone);
        return Maybe.fromPublisher(
                repository.findByPhone(phone)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Wallet> findByImei(String imei) {
        log.debug("Finding wallet by imei: {}", imei);
        return Maybe.fromPublisher(
                repository.findByImei(imei)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Flowable<Wallet> findAll() {
        log.debug("Finding all wallets");
        return Flowable.fromPublisher(
                repository.findAll()
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Completable deleteById(String id) {
        log.debug("Deleting wallet by id: {}", id);
        return Completable.fromPublisher(
                repository.deleteById(id)
        );
    }
}