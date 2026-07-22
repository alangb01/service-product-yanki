package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;


import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;

public interface WalletRepositoryPort {

    Single<Wallet> save(Wallet wallet);

    Maybe<Wallet> findById(String id);

    Maybe<Wallet> findByPhone(String phone);

    Maybe<Wallet> findByImei(String imei);

    Flowable<Wallet> findAll();

    Completable deleteById(String id);
}