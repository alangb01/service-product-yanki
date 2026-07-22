package pe.nom.charlygastelo.app.yankiservice.application.command;

import java.math.BigDecimal;

public record WalletPaymentCommand(
    String sourceWalletId,
    String targetWalletid,
    BigDecimal ammount,
    String description
) { }
