package pe.nom.charlygastelo.app.yankiservice.infrastructure.events;

import java.time.Instant;
import java.util.UUID;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.shared.avro.dto.CardRequestEvent;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Card;
import pe.nom.charlygastelo.app.yankiservice.domain.port.CardEventPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardKafkaClient implements CardEventPort {

    private final CardRequestProducer requestProducer;
    private final CardResponseRegistry responseRegistry;

    @Override
    public Single<Card> getById(String cardId) {
        String correlationId = UUID.randomUUID().toString();

        CardRequestEvent event =
                CardRequestEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("CARD_REQUEST")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("yanki-service")
                        .setCorrelationId(correlationId)
                        .setCardId(cardId)
                        .build();

        log.info("Requesting card information. cardId={}, correlationId={}",
                cardId, correlationId);

        return responseRegistry.waitForResponse(correlationId)
                .doOnSubscribe(disposable ->
                        requestProducer.send(correlationId, event)
                )
                .doOnSuccess(card ->
                        log.info("Card response received. cardId={}, correlationId={}",
                                card.id(), correlationId))
                .doOnError(error ->
                        log.error("Error requesting card information. cardId={}, correlationId={}, reason={}",
                                cardId, correlationId, error.getMessage(), error));
    }
}