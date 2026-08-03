package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardRequestEvent;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.DebitCardRequestProducerPort;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class DebitCardKafkaProducer extends BaseEventProducer implements DebitCardRequestProducerPort {
    public DebitCardKafkaProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Value("${topic.debit-card-request}")
    private String debitCardRequestTopic;

    @Override
    public Completable publishDebitCardRequest(String correlationId, String debitCardId) {
        DebitCardRequestEvent event = DebitCardRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("DEBIT_CARD_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setCorrelationId(correlationId)
                .setDebitCardId(debitCardId)
                .build();


        return publish(debitCardRequestTopic, debitCardId, event);
    }
}
