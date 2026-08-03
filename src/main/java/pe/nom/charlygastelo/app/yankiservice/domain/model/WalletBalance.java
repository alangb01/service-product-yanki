package pe.nom.charlygastelo.app.yankiservice.domain.model;

public record WalletBalance (
        Account account,
        DebitCard debitCard,
        WalletLink walletLink,
        Wallet wallet
) { }
