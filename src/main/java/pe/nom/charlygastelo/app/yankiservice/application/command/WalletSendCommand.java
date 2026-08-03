package pe.nom.charlygastelo.app.yankiservice.application.command;

import java.math.BigDecimal;

public record WalletSendCommand(
    String walletSourceId,
    String customerId,
    String targetNumber,
    BigDecimal amount,
    String description
) { }
