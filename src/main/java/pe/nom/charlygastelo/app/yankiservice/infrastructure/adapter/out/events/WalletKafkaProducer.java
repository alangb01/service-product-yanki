package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events.mapper.WalletEventMapper;

@Slf4j
@Component
public class WalletKafkaProducer extends BaseEventProducer implements WalletEventProducerPort {

    private final WalletEventMapper mapper;

    @Value("${topic.wallet-created}")
    private String walletCreatedTopic;

    @Value("${topic.wallet-updated}")
    private String walletUpdatedTopic;

    @Value("${topic.wallet-deleted}")
    private String walletDeletedTopic;

    public WalletKafkaProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate, WalletEventMapper mapper) {
        super(kafkaTemplate);
        this.mapper = mapper;
    }

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
}