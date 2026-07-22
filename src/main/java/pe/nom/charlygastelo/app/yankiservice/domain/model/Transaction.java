package pe.nom.charlygastelo.app.yankiservice.domain.model;

import pe.nom.charlygastelo.app.yankiservice.domain.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        String id,
        String customerId,
        String sourceProductType,
        String targetProductType,
        String sourceProductId,
        String targetProductId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal commission,
        String description,
        Instant timestamp
) {

    private void validateAmount() {
        if (this.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
    }

}