package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@Builder
public class WalletLinkDocument {

    @Id
    private String id;

    private String walletId;
    private String debitCardId;
    private String accountId;

    private Instant linkedAt;
}
