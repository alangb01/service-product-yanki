package pe.nom.charlygastelo.app.yankiservice.application.command;

public record WalletLinkCommand (
    String walletId,
    String debitCardId
) { }
