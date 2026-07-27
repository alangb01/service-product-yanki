package pe.nom.charlygastelo.app.yankiservice.application.command;

import java.math.BigDecimal;

public record WalletPaymentCommand(
    String walletSourceId,
    String customerId,
    String sourceNumber,
    String targetNumber,
    BigDecimal amount,
    String description
) { }
