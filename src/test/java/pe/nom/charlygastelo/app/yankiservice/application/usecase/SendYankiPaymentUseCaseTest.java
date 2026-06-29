package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.InsufficientWalletBalanceException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.*;
import pe.nom.charlygastelo.app.yankiservice.domain.port.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class SendYankiPaymentUseCaseTest {

    private WalletRepositoryPort repository;
    private MovementEventPort movementEventPort;
    private WalletEventProducerPort producer;
    private SendYankiPaymentUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(WalletRepositoryPort.class);
        movementEventPort = mock(MovementEventPort.class);
        producer = mock(WalletEventProducerPort.class);

        useCase = new SendYankiPaymentUseCase(repository, movementEventPort, producer);
    }

    @Test
    void shouldSendYankiPaymentSuccessfully() {
        Wallet source = wallet("wallet-1", "999111111", BigDecimal.valueOf(100));
        Wallet target = wallet("wallet-2", "999222222", BigDecimal.ZERO);

        when(repository.findByPhone("999111111")).thenReturn(Maybe.just(source));
        when(repository.findByPhone("999222222")).thenReturn(Maybe.just(target));
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> Single.just(invocation.getArgument(0)));
        when(movementEventPort.registerMovement(any(), any(), anyString())).thenReturn(Completable.complete());
        when(producer.publishYankiPaymentCompleted(any())).thenReturn(Completable.complete());

        useCase.execute("999111111", "999222222", BigDecimal.valueOf(50), "test")
                .test()
                .assertComplete();

        verify(repository, times(2)).save(any(Wallet.class));
        verify(producer).publishYankiPaymentCompleted(any());
    }

    @Test
    void shouldFailWhenInsufficientBalance() {
        Wallet source = wallet("wallet-1", "999111111", BigDecimal.TEN);
        Wallet target = wallet("wallet-2", "999222222", BigDecimal.ZERO);

        when(repository.findByPhone("999111111")).thenReturn(Maybe.just(source));
        when(repository.findByPhone("999222222")).thenReturn(Maybe.just(target));

        useCase.execute("999111111", "999222222", BigDecimal.valueOf(50), "test")
                .test()
                .assertError(InsufficientWalletBalanceException.class);

        verify(repository, never()).save(any());
    }

    private Wallet wallet(String id, String phone, BigDecimal balance) {
        return new Wallet(
                id,
                DocumentType.DNI,
                "12345678",
                phone,
                "imei-" + id,
                id + "@mail.com",
                null,
                balance,
                WalletStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}