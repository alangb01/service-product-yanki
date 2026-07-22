package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountResponseEvent;

import java.util.function.Consumer;

@Component
@Slf4j
public class AccountKafkaConsumer {

    private Consumer<AccountResponseEvent> handler;

    @KafkaListener(
            topics = "${topic.account-response}",
            groupId = "yanki-service-${random.uuid}"
    )
    public void listen(AccountResponseEvent event) {

        log.info(
                "[ACCOUNT-RESPONSE] Event received. correlationId={}, accountId={}",
                event.getCorrelationId(),
                event.getAccount().getId()
        );

        if (handler == null) {
            log.warn(
                    "[ACCOUNT-RESPONSE] No handler registered. correlationId={}",
                    event.getCorrelationId()
            );
            return;
        }

        try {
            handler.accept(event);

            log.debug(
                    "[ACCOUNT-RESPONSE] Event processed successfully. correlationId={}",
                    event.getCorrelationId()
            );

        } catch (Exception ex) {

            log.error(
                    "[ACCOUNT-RESPONSE] Error processing event. correlationId={}",
                    event.getCorrelationId(),
                    ex
            );

            throw ex;
        }
    }

    public void registerHandler(
            Consumer<AccountResponseEvent> handler
    ) {
        this.handler = handler;

        log.info(
                "[ACCOUNT-RESPONSE] Handler registered successfully."
        );
    }

}