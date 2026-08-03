package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WalletSendRequest(
        String customerId,

        @NotBlank
        String targetPhone,

        @NotNull
        BigDecimal amount,

        String description

) {
}