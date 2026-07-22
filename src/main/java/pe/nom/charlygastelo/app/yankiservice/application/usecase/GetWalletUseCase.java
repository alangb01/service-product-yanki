package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetWalletUseCase {

    private final WalletRepositoryPort repository;

    public Maybe<Wallet> byId(String id) {
        log.info("Searching wallet by id={}", id);

        return repository.findById(id)
                .switchIfEmpty(Maybe.error(
                        new WalletNotFoundException("Wallet not found: " + id)
                ))
                .doOnSuccess(wallet ->
                        log.info("Wallet found. walletId={}, phone={}",
                                wallet.id(), wallet.phone()))
                .doOnError(error ->
                        log.error("Error searching wallet by id. id={}, reason={}",
                                id, error.getMessage(), error));
    }

    public Maybe<Wallet> byPhone(String phone) {
        log.info("Searching wallet by phone={}", phone);

        return repository.findByPhone(phone)
                .switchIfEmpty(Maybe.error(
                        new WalletNotFoundException("Wallet not found with phone: " + phone)
                ))
                .doOnSuccess(wallet ->
                        log.info("Wallet found by phone. walletId={}, phone={}",
                                wallet.id(), wallet.phone()))
                .doOnError(error ->
                        log.error("Error searching wallet by phone. phone={}, reason={}",
                                phone, error.getMessage(), error));
    }
}