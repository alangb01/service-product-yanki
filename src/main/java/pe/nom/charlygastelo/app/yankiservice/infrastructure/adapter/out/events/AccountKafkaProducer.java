package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events;

import io.reactivex.rxjava3.core.Completable;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountRequestEvent;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.AccountRequestProducerPort;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class AccountKafkaProducer extends BaseEventProducer implements AccountRequestProducerPort {
    public AccountKafkaProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Value("${topic.account-request}")
    private String accountRequestTopic;


    public Completable publishAccountRequest(String accountId, String correlationId) {
        AccountRequestEvent event = AccountRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("ACCOUNT_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("yanki-service")
                .setCorrelationId(correlationId)
                .setAccountId(accountId)
                .build();


        return publish(accountRequestTopic, accountId, event);
    }
}
