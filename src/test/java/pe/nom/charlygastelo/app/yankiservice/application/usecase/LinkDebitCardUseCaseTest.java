package pe.nom.charlygastelo.app.yankiservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.nom.charlygastelo.app.yankiservice.domain.exception.InvalidDebitCardException;
import pe.nom.charlygastelo.app.yankiservice.domain.model.*;
import pe.nom.charlygastelo.app.yankiservice.domain.port.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class LinkDebitCardUseCaseTest {

    private WalletRepositoryPort repository;
    private CardEventPort cardEventPort;
    private WalletEventProducerPort producer;
    private LinkDebitCardUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(WalletRepositoryPort.class);
        cardEventPort = mock(CardEventPort.class);
        producer = mock(WalletEventProducerPort.class);

        useCase = new LinkDebitCardUseCase(repository, cardEventPort, producer);
    }

    @Test
    void shouldLinkDebitCardSuccessfully() {
        Wallet wallet = wallet();
        Card card = new Card("card-1", "cus-1", "acc-1", "1234", "DEBIT", "ACTIVE");

        when(repository.findById("wallet-1")).thenReturn(io.reactivex.rxjava3.core.Maybe.just(wallet));
        when(cardEventPort.getById("card-1")).thenReturn(Single.just(card));
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> Single.just(invocation.getArgument(0)));
        when(producer.publishWalletLinkedDebitCard(any(Wallet.class))).thenReturn(Completable.complete());

        useCase.execute("wallet-1", "card-1")
                .test()
                .assertComplete()
                .assertValue(result -> "card-1".equals(result.debitCardId()));
    }

    @Test
    void shouldFailWhenCardIsNotDebit() {
        Wallet wallet = wallet();
        Card card = new Card("card-1", "cus-1", "acc-1", "1234", "CREDIT", "ACTIVE");

        when(repository.findById("wallet-1")).thenReturn(io.reactivex.rxjava3.core.Maybe.just(wallet));
        when(cardEventPort.getById("card-1")).thenReturn(Single.just(card));

        useCase.execute("wallet-1", "card-1")
                .test()
                .assertError(InvalidDebitCardException.class);

        verify(repository, never()).save(any());
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