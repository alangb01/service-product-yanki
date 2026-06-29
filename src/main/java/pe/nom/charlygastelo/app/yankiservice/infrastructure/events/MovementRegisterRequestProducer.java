package pe.nom.charlygastelo.app.yankiservice.infrastructure.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.shared.avro.dto.MovementRegisterRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementRegisterRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer avroJsonSerializer;

    @Value("${topic.movement-register-request}")
    private String movementRegisterRequestTopic;

    public void send(String correlationId, MovementRegisterRequestEvent event) {
        try {
            String payload = avroJsonSerializer.serialize(event);

            log.info(
                    "Sending MovementRegisterRequestEvent. topic={}, correlationId={}, transactionId={}, productId={}",
                    movementRegisterRequestTopic,
                    correlationId,
                    event.getTransactionId(),
                    event.getProductId()
            );

            kafkaTemplate.send(movementRegisterRequestTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error(
                                    "Error sending MovementRegisterRequestEvent. topic={}, correlationId={}, reason={}",
                                    movementRegisterRequestTopic,
                                    correlationId,
                                    error.getMessage(),
                                    error
                            );
                            return;
                        }

                        log.info(
                                "MovementRegisterRequestEvent sent successfully. topic={}, correlationId={}, partition={}, offset={}",
                                movementRegisterRequestTopic,
                                correlationId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });

        } catch (Exception e) {
            log.error(
                    "Error serializing MovementRegisterRequestEvent. correlationId={}, reason={}",
                    correlationId,
                    e.getMessage(),
                    e
            );
        }
    }
}