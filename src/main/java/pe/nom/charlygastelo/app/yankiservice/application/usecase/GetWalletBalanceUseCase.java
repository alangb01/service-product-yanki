package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;

@RequiredArgsConstructor
public class GetWalletBalanceUseCase {

    private final WalletRepositoryPort repository;

    public Single<Wallet> execute(String id) {
        return repository.findById(id)
                .switchIfEmpty(Single.error(
                        new WalletNotFoundException("Wallet not found: " + id)
                ));
    }
}