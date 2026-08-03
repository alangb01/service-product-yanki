package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseEventProducer {
    protected final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    protected Completable publish(String topic, String key, SpecificRecordBase event) {
        log.info("Sending event. topic={}, key={}", topic, key);
        log.debug("Serializing event. event={}", event);
        return Completable.fromFuture(
            kafkaTemplate.send(topic, key, event)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Error sending event. topic={}, key={}, reason={}",
                            topic, key, error.getMessage(), error);
                    }
                    else {
                        log.info("Event sent successfully. topic={}, key={}, partition={}, offset={}",
                            topic, key,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    }
                })
        );
    }
}
