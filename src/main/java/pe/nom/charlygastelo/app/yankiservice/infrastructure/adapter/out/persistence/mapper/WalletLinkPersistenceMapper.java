package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.document.WalletLinkDocument;

@Mapper(componentModel = "spring")
public interface WalletLinkPersistenceMapper {
    WalletLink toDomain(WalletLinkDocument document);
    WalletLinkDocument toDocument(WalletLink domain);
}
