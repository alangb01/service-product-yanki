package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ListWalletsUseCaseTest {

    private WalletRepositoryPort repository;
    private ListWalletsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(WalletRepositoryPort.class);
        useCase = new ListWalletsUseCase(repository);
    }

    @Test
    void shouldListWallets() {
        Wallet wallet = wallet();

        when(repository.findAll())
                .thenReturn(Flowable.just(wallet));

        useCase.execute()
                .test()
                .assertComplete()
                .assertValue(wallet);

        verify(repository).findAll();
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