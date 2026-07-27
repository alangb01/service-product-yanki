package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events.mapper;

import java.time.Instant;
import java.util.UUID;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.shared.avro.dto.*;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;

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

    public WalletLinkedDebitCardEvent toWalletLinkedDebitCardEvent(WalletLink walletLink, Wallet wallet) {
        return WalletLinkedDebitCardEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("WALLET_LINKED_DEBIT_CARD")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setWalletId(value(wallet.id()))
                .setPhone(value(wallet.phone()))
                .setDebitCardId(walletLink.getDebitCardId())
                .build();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    public SpecificRecordBase toWalletUnlinkedDebitCardEvent(Wallet wallet) {
        return WalletUnlinkedDebitCardEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("WALLET_UNLINKED_DEBIT_CARD")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setWalletId(value(wallet.id()))
                .setPhone(value(wallet.phone()))
                .build();
    }
}