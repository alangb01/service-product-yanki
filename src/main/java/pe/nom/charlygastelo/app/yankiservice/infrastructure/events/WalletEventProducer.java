package pe.nom.charlygastelo.app.yankiservice.infrastructure.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.YankiTransaction;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.events.mapper.WalletEventMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventProducer implements WalletEventProducerPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer avroJsonSerializer;
    private final WalletEventMapper mapper;

    @Value("${topic.wallet-created}")
    private String walletCreatedTopic;

    @Value("${topic.wallet-updated}")
    private String walletUpdatedTopic;

    @Value("${topic.wallet-deleted}")
    private String walletDeletedTopic;

    @Value("${topic.wallet-linked-debit-card}")
    private String walletLinkedDebitCardTopic;

    @Value("${topic.yanki-payment-completed}")
    private String yankiPaymentCompletedTopic;

    @Value("${topic.yanki-payment-failed}")
    private String yankiPaymentFailedTopic;

    @Override
    public Completable publishWalletCreated(Wallet wallet) {
        return publish(
                walletCreatedTopic,
                wallet.id(),
                mapper.toWalletCreatedEvent(wallet)
        );
    }

    @Override
    public Completable publishWalletUpdated(Wallet wallet) {
        return publish(
                walletUpdatedTopic,
                wallet.id(),
                mapper.toWalletUpdatedEvent(wallet)
        );
    }

    @Override
    public Completable publishWalletDeleted(Wallet wallet) {
        return publish(
                walletDeletedTopic,
                wallet.id(),
                mapper.toWalletDeletedEvent(wallet)
        );
    }

    @Override
    public Completable publishWalletLinkedDebitCard(Wallet wallet) {
        return publish(
                walletLinkedDebitCardTopic,
                wallet.id(),
                mapper.toWalletLinkedDebitCardEvent(wallet)
        );
    }

    @Override
    public Completable publishYankiPaymentCompleted(YankiTransaction transaction) {
        return publish(
                yankiPaymentCompletedTopic,
                transaction.id(),
                mapper.toYankiPaymentCompletedEvent(transaction)
        );
    }

    @Override
    public Completable publishYankiPaymentFailed(
            YankiTransaction transaction,
            String reason) {

        return publish(
                yankiPaymentFailedTopic,
                transaction.id(),
                mapper.toYankiPaymentFailedEvent(transaction, reason)
        );
    }

    private Completable publish(
            String topic,
            String key,
            SpecificRecordBase event) {

        return Completable.create(emitter -> {
            try {
                String payload = avroJsonSerializer.serialize(event);

                kafkaTemplate.send(topic, key, payload)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error(
                                        "Error publishing wallet event. topic={}, key={}, eventClass={}, reason={}",
                                        topic,
                                        key,
                                        event.getClass().getSimpleName(),
                                        error.getMessage(),
                                        error
                                );
                                emitter.onError(error);
                                return;
                            }

                            log.info(
                                    "Wallet event published successfully. topic={}, key={}, eventClass={}, partition={}, offset={}",
                                    topic,
                                    key,
                                    event.getClass().getSimpleName(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );

                            emitter.onComplete();
                        });

            } catch (Exception e) {
                log.error(
                        "Error serializing wallet event. topic={}, key={}, eventClass={}, reason={}",
                        topic,
                        key,
                        event.getClass().getSimpleName(),
                        e.getMessage(),
                        e
                );
                emitter.onError(e);
            }
        });
    }
}