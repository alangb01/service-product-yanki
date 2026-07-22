package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.document.WalletDocument;

@Component
public class WalletPersistentMapper {

    public WalletDocument toDocument(Wallet wallet) {
        return WalletDocument.builder()
                .id(wallet.id())
                .documentType(wallet.documentType())
                .documentNumber(wallet.documentNumber())
                .phone(wallet.phone())
                .imei(wallet.imei())
                .email(wallet.email())
                .status(wallet.status())
                .createdAt(wallet.createdAt())
                .updatedAt(wallet.updatedAt())
                .build();
    }

    public Wallet toDomain(WalletDocument document) {
        return new Wallet(
                document.getId(),
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getPhone(),
                document.getImei(),
                document.getEmail(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}