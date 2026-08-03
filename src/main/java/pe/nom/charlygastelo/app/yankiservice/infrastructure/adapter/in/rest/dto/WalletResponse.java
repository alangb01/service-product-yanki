package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(

        String id,

        String documentType,

        String documentNumber,

        String phone,

        String imei,

        String email,

        String status,

        String createdAt,

        String updatedAt

) {
}