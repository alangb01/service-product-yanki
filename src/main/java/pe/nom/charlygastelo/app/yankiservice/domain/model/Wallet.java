package pe.nom.charlygastelo.app.yankiservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Wallet(
        String id,
        DocumentType documentType,
        String documentNumber,
        String phone,
        String imei,
        String email,
        String debitCardId,
        BigDecimal balance,
        WalletStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public boolean isActive() {
        return status == WalletStatus.ACTIVE;
    }

    public boolean hasLinkedDebitCard() {
        return debitCardId != null && !debitCardId.isBlank();
    }

    public Wallet linkDebitCard(String cardId) {
        return new Wallet(
                id,
                documentType,
                documentNumber,
                phone,
                imei,
                email,
                cardId,
                balance,
                status,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Wallet unlinkDebitCard() {
        return new Wallet(
                id,
                documentType,
                documentNumber,
                phone,
                imei,
                email,
                null,
                balance,
                status,
                createdAt,
                LocalDateTime.now()
        );
    }

    public Wallet withBalance(BigDecimal newBalance) {
        return new Wallet(
                id,
                documentType,
                documentNumber,
                phone,
                imei,
                email,
                debitCardId,
                newBalance,
                status,
                createdAt,
                LocalDateTime.now()
        );
    }
}