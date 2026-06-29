package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

public record WalletBalanceResponse(

        String walletId,

        String phone,

        BigDecimal balance

) {
}