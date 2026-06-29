package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkDebitCardRequest(

        @NotBlank
        String cardId

) {
}