package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListWalletsUseCase {

    private final WalletRepositoryPort repository;

    public Flowable<Wallet> execute() {
        log.info("Listing wallets");

        return repository.findAll()
                .doOnComplete(() ->
                        log.info("Wallet list completed successfully"))
                .doOnError(error ->
                        log.error("Error listing wallets. reason={}",
                                error.getMessage(), error));
    }
}