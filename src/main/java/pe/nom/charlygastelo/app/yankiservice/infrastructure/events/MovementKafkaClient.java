package pe.nom.charlygastelo.app.yankiservice.infrastructure.events;

import java.time.Instant;
import java.util.UUID;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.shared.avro.dto.MovementRegisterRequestEvent;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransaction;
import pe.nom.charlygastelo.app.yankiservice.domain.port.MovementEventPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementKafkaClient implements MovementEventPort {

    private final MovementRegisterRequestProducer producer;

    @Override
    public Completable registerMovement(
            YankiTransaction transaction,
            Wallet wallet,
            String movementType) {

        return Completable.fromRunnable(() -> {
            String correlationId = UUID.randomUUID().toString();

            MovementRegisterRequestEvent event =
                    MovementRegisterRequestEvent.newBuilder()
                            .setEventId(UUID.randomUUID().toString())
                            .setEventType("MOVEMENT_REGISTER_REQUEST")
                            .setOccurredAt(Instant.now().toString())
                            .setVersion("1.0")
                            .setSource("yanki-service")
                            .setCorrelationId(correlationId)
                            .setCustomerId(wallet.documentNumber())
                            .setProductId(wallet.id())
                            .setProductType("WALLET")
                            .setMovementType(movementType)
                            .setAmount(transaction.amount().doubleValue())
                            .setBalanceAfter(wallet.balance().doubleValue())
                            .setTransactionId(transaction.id())
                            .setDescription(transaction.description() == null ? "" : transaction.description())
                            .build();

            producer.send(correlationId, event);

            log.info(
                    "MovementRegisterRequestEvent sent. correlationId={}, walletId={}, transactionId={}, movementType={}",
                    correlationId,
                    wallet.id(),
                    transaction.id(),
                    movementType
            );
        });
    }
}