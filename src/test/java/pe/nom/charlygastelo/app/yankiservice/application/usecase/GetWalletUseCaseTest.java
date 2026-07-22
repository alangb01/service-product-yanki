package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class GetWalletUseCaseTest {

    private WalletRepositoryPort repository;
    private GetWalletUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(WalletRepositoryPort.class);
        useCase = new GetWalletUseCase(repository);
    }

    @Test
    void shouldGetWalletById() {
        Wallet wallet = wallet();

        when(repository.findById("wallet-1"))
                .thenReturn(Maybe.just(wallet));

        useCase.byId("wallet-1")
                .test()
                .assertComplete()
                .assertValue(wallet);
    }

    @Test
    void shouldFailWhenWalletNotFoundById() {
        when(repository.findById("wallet-1"))
                .thenReturn(Maybe.empty());

        useCase.byId("wallet-1")
                .test()
                .assertError(WalletNotFoundException.class);
    }

    @Test
    void shouldGetWalletByPhone() {
        Wallet wallet = wallet();

        when(repository.findByPhone("999999999"))
                .thenReturn(Maybe.just(wallet));

        useCase.byPhone("999999999")
                .test()
                .assertComplete()
                .assertValue(wallet);
    }

    @Test
    void shouldFailWhenWalletNotFoundByPhone() {
        when(repository.findByPhone("999999999"))
                .thenReturn(Maybe.empty());

        useCase.byPhone("999999999")
                .test()
                .assertError(WalletNotFoundException.class);
    }

    private Wallet wallet() {
        return new Wallet(
                "wallet-1",
                DocumentType.DNI,
                "12345678",
                "999999999",
                "imei-1",
                "test@mail.com",
                null,
                BigDecimal.ZERO,
                WalletStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}