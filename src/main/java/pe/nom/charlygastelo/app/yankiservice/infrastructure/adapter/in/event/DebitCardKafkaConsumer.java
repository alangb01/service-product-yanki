package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.event;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.DebitCardResponseEvent;

import java.util.function.Consumer;

@Component
@Slf4j
public class DebitCardKafkaConsumer {

    private Consumer<DebitCardResponseEvent> handler;

    @KafkaListener(
            topics = "${topic.debit-card-response}",
            groupId = "yanki-service-${random.uuid}"
    )
    public void onMessage(DebitCardResponseEvent event) {
        log.info("[KAFKA] Received DebitCardResponseEvent correlationId={}", event.getCorrelationId());

        if (handler == null) {
            log.error("[KAFKA] No handler registered for correlationId={}", event.getCorrelationId());
            return;
        }

        handler.accept(event);
    }

    public void registerHandler(Consumer<DebitCardResponseEvent> handler) {
        this.handler = handler;
    }
}
