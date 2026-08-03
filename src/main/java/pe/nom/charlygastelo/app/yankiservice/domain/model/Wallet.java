package pe.nom.charlygastelo.app.yankiservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record Wallet(
        String id,
        DocumentType documentType,
        String documentNumber,
        String phone,
        String imei,
        String email,
        WalletStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public boolean isActive() {
        return status == WalletStatus.ACTIVE;
    }

}