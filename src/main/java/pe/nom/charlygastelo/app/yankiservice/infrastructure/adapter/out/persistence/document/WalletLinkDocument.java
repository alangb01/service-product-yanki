package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallet_links")
public class WalletLinkDocument {

    @Id
    private String id;
    private String walletId;
    private String debitCardId;
    private Instant linkedAt;
}
