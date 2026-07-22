package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletLink;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletLinkEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events.mapper.WalletEventMapper;

@Component
@Slf4j
public class WalletLinkKafkaProducer extends BaseEventProducer implements WalletLinkEventProducerPort {

    private final WalletEventMapper mapper;

    @Value("${topic.wallet-linked-debit-card}")
    private String walletLinkedDebitCardTopic;

    @Value("${topic.wallet-unlinked-debit-card}")
    private String walletUnlinkedDebitCardTopic;

    public WalletLinkKafkaProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate, WalletEventMapper mapper) {
        super(kafkaTemplate);
        this.mapper = mapper;
    }

    @Override
    public Completable publishWalletLinkedDebitCard(WalletLink walletLink, Wallet wallet) {
        return publish(
                walletLinkedDebitCardTopic,
                walletLink.getWalletId(),
                mapper.toWalletLinkedDebitCardEvent(walletLink, wallet)
        );
    }

    @Override
    public Completable publishWalletUnlinkedDebitCard() {
        log.info("NOT IMPLEMENTED");
        return Completable.complete();
    }
}
