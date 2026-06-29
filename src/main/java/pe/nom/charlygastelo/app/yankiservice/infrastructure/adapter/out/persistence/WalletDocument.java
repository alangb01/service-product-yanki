package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallets")
public class WalletDocument {

    @Id
    private String id;

    private DocumentType documentType;

    private String documentNumber;

    @Indexed(unique = true)
    private String phone;

    @Indexed(unique = true)
    private String imei;

    private String email;

    private String debitCardId;

    private BigDecimal balance;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}