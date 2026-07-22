package pe.nom.charlygastelo.app.yankiservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@Builder
public class WalletLink {

    @Id
    private String id;
    private String walletId;
    private String debitCardId;
    private Instant linkedAt;

    public WalletLink unlinkDebitCard() {
        return WalletLink.builder()
                .id(id)
                .walletId(walletId)
                .linkedAt(linkedAt)
                .build();
    }
}
