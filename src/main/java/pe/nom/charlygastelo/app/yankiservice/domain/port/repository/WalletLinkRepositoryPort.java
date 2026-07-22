package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;

public interface WalletLinkRepositoryPort {

    Single<WalletLink> save(WalletLink link);

    Maybe<WalletLink> findByWalletId(String walletId);

    Completable delete(String id);
}
