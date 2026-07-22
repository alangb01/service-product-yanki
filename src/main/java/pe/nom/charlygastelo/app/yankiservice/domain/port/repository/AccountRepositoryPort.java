package pe.nom.charlygastelo.app.yankiservice.domain.port.repository;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Account;

public interface AccountRepositoryPort {
    Single<Account> getById(String accountId);
}
