package pe.nom.charlygastelo.app.yankiservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record YankiTransaction(
        String id,
        String sourceWalletId,
        String targetWalletId,
        String sourcePhone,
        String targetPhone,
        YankiTransactionType type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
}