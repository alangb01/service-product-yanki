package pe.nom.charlygastelo.app.yankiservice.infrastructure.events.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.shared.avro.dto.WalletCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletDeletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletLinkedDebitCardEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletUpdatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.YankiPaymentCompletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.YankiPaymentFailedEvent;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransaction;

@Component
public class WalletEventMapper {

    public WalletCreatedEvent toWalletCreatedEvent(Wallet wallet) {
        return WalletCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("WALLET_CREATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setWalletId(value(wallet.id()))
                .setDocumentType(value(wallet.documentType().name()))
                .setDocumentNumber(value(wallet.documentNumber()))
                .setPhone(value(wallet.phone()))
                .setImei(value(wallet.imei()))
                .setEmail(value(wallet.email()))
                .setBalance(wallet.balance().doubleValue())
                .setStatus(wallet.status().name())
                .build();
    }

    public WalletUpdatedEvent toWalletUpdatedEvent(Wallet wallet) {
        return WalletUpdatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("WALLET_UPDATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setWalletId(value(wallet.id()))
                .setPhone(value(wallet.phone()))
                .setEmail(value(wallet.email()))
                .setDebitCardId(value(wallet.debitCardId()))
                .setBalance(wallet.balance().doubleValue())
                .setStatus(wallet.status().name())
                .build();
    }

    public WalletDeletedEvent toWalletDeletedEvent(Wallet wallet) {
        return WalletDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("WALLET_DELETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setWalletId(value(wallet.id()))
                .setPhone(value(wallet.phone()))
                .setStatus(wallet.status().name())
                .build();
    }

    public WalletLinkedDebitCardEvent toWalletLinkedDebitCardEvent(Wallet wallet) {
        return WalletLinkedDebitCardEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("WALLET_LINKED_DEBIT_CARD")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setWalletId(value(wallet.id()))
                .setPhone(value(wallet.phone()))
                .setDebitCardId(value(wallet.debitCardId()))
                .build();
    }

    public YankiPaymentCompletedEvent toYankiPaymentCompletedEvent(
            YankiTransaction transaction) {

        return YankiPaymentCompletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("YANKI_PAYMENT_COMPLETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setTransactionId(value(transaction.id()))
                .setSourceWalletId(value(transaction.sourceWalletId()))
                .setTargetWalletId(value(transaction.targetWalletId()))
                .setSourcePhone(value(transaction.sourcePhone()))
                .setTargetPhone(value(transaction.targetPhone()))
                .setAmount(transaction.amount().doubleValue())
                .setDescription(value(transaction.description()))
                .build();
    }

    public YankiPaymentFailedEvent toYankiPaymentFailedEvent(
            YankiTransaction transaction,
            String reason) {

        return YankiPaymentFailedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("YANKI_PAYMENT_FAILED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setTransactionId(value(transaction.id()))
                .setSourceWalletId(value(transaction.sourceWalletId()))
                .setTargetWalletId(value(transaction.targetWalletId()))
                .setSourcePhone(value(transaction.sourcePhone()))
                .setTargetPhone(value(transaction.targetPhone()))
                .setAmount(transaction.amount().doubleValue())
                .setReason(value(reason))
                .build();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}