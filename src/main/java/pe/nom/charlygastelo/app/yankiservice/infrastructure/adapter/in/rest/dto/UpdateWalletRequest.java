package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateWalletRequest(

        @NotBlank
        String email,

        @NotBlank
        String status

) {
}