package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.mapper;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.WalletDocument;

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
                .debitCardId(wallet.debitCardId())
                .balance(wallet.balance())
                .status(wallet.status().name())
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
                document.getDebitCardId(),
                document.getBalance(),
                WalletStatus.valueOf(document.getStatus()),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}