package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class GetWalletBalanceUseCaseTest {

    private WalletRepositoryPort repository;
    private GetWalletBalanceUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(WalletRepositoryPort.class);
        useCase = new GetWalletBalanceUseCase(repository);
    }

    @Test
    void shouldGetWalletBalance() {
        Wallet wallet = wallet();

        when(repository.findById("wallet-1"))
                .thenReturn(Maybe.just(wallet));

        useCase.execute("wallet-1")
                .test()
                .assertComplete()
                .assertValue(wallet);
    }

    @Test
    void shouldFailWhenWalletNotFound() {
        when(repository.findById("wallet-1"))
                .thenReturn(Maybe.empty());

        useCase.execute("wallet-1")
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
                BigDecimal.valueOf(100),
                WalletStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}