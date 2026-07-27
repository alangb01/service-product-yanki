package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.nom.charlygastelo.app.shared.avro.dto.WalletPaymentOccurredEvent;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletPaymentCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletValidationResult;

@Mapper(componentModel = "spring")
public interface WalletPaymentEventMapper {

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "eventType", constant = "WALLET_PAYMENT")
    @Mapping(target = "occurredAt", expression = "java(java.time.Instant.now().toString())")
    @Mapping(target = "version", constant = "1.0")
    @Mapping(target = "source", constant = "yanki-service")

    @Mapping(target = "correlationId", source = "correlationId")

    @Mapping(target = "customerId", source = "cmd.customerId")

    @Mapping(target = "sourceWalletId", source = "source.wallet.id")
    @Mapping(target = "targetWalletId", source = "target.wallet.id")

    @Mapping(target = "sourceDebitCardId", source = "source.link.debitCardId")
    @Mapping(target = "targetDebitCardId", source = "target.link.debitCardId")

    @Mapping(target = "amount", source = "cmd.amount")
    @Mapping(target = "description", source = "cmd.description")
    WalletPaymentOccurredEvent toWalletPaymentOccurredEvent(
            WalletValidationResult source,
            WalletValidationResult target,
            WalletPaymentCommand cmd,
            String correlationId
    );
}
