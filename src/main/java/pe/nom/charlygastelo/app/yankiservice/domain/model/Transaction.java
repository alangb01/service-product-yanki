package pe.nom.charlygastelo.app.yankiservice.domain.model;

import pe.nom.charlygastelo.app.yankiservice.domain.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        String id,
        String sourceWalletId,
        String targetWalletId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal commission,
        String description
) {

    private void validateAmount() {
        if (this.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
    }

}