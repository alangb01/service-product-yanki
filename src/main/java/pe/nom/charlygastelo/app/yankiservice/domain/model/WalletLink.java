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
    private String accountId;

    private Instant linkedAt;

    public WalletLink unlinkDebitCard() {
        return WalletLink.builder()
                .id(id)
                .walletId(walletId)
                .accountId(accountId)
                .linkedAt(linkedAt)
                .build();
    }
}
