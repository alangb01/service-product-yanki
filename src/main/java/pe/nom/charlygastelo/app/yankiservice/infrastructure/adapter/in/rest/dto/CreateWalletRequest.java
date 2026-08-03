package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateWalletRequest(

        @NotBlank
        String documentType,

        @NotBlank
        String documentNumber,

        @NotBlank
        String phone,

        @NotBlank
        String imei,

        @Email
        @NotBlank
        String email

) {
}