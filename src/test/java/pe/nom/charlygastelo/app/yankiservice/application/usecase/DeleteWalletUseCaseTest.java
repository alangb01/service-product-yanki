package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.WalletNotFoundException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class DeleteWalletUseCaseTest {

    private WalletRepositoryPort repository;
    private WalletEventProducerPort producer;
    private DeleteWalletUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(WalletRepositoryPort.class);
        producer = mock(WalletEventProducerPort.class);
        useCase = new DeleteWalletUseCase(repository, producer);
    }

    @Test
    void shouldDeleteWalletSuccessfully() {
        Wallet wallet = wallet();

        when(repository.findById("wallet-1"))
                .thenReturn(Maybe.just(wallet));

        when(repository.deleteById("wallet-1"))
                .thenReturn(Completable.complete());

        when(producer.publishWalletDeleted(wallet))
                .thenReturn(Completable.complete());

        useCase.execute("wallet-1")
                .test()
                .assertComplete();

        verify(repository).deleteById("wallet-1");
        verify(producer).publishWalletDeleted(wallet);
    }

    @Test
    void shouldFailWhenWalletNotFound() {
        when(repository.findById("wallet-1"))
                .thenReturn(Maybe.empty());

        useCase.execute("wallet-1")
                .test()
                .assertError(WalletNotFoundException.class);

        verify(repository, never()).deleteById(anyString());
        verify(producer, never()).publishWalletDeleted(any());
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