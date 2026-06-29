package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.mapper.WalletPersistentMapper;

@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final ReactiveWalletRepository repository;
    private final WalletPersistentMapper mapper;

    @Override
    public Single<Wallet> save(Wallet wallet) {
        return Single.fromPublisher(
                repository.save(mapper.toDocument(wallet))
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Wallet> findById(String id) {
        return Maybe.fromPublisher(
                repository.findById(id)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Wallet> findByPhone(String phone) {
        return Maybe.fromPublisher(
                repository.findByPhone(phone)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Wallet> findByImei(String imei) {
        return Maybe.fromPublisher(
                repository.findByImei(imei)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Flowable<Wallet> findAll() {
        return Flowable.fromPublisher(
                repository.findAll()
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromPublisher(
                repository.deleteById(id)
        );
    }
}