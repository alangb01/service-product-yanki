package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.yankiservice.application.command.WalletPaymentCommand;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletValidationResult;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.event.WalletPaymentEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events.mapper.WalletEventMapper;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.events.mapper.WalletPaymentEventMapper;

@Slf4j
@Component
public class WalletPaymentKafkaProducer extends BaseEventProducer implements WalletPaymentEventProducerPort {

    private final WalletPaymentEventMapper mapper;

    @Value("${topic.wallet-payment-occurred}")
    private String walletPaymentOccurredTopic;

    public WalletPaymentKafkaProducer(
            KafkaTemplate<String, SpecificRecordBase> kafkaTemplate,
            WalletPaymentEventMapper mapper
    ) {
        super(kafkaTemplate);
        this.mapper = mapper;
    }

    @Override
    public Completable publishWalletPaymentOccurred(
            WalletValidationResult source,
            WalletValidationResult target,
            WalletPaymentCommand cmd,
            String correlationId
        ) {
        return publish(
                walletPaymentOccurredTopic,
                correlationId,
                mapper.toWalletPaymentOccurredEvent(source, target, cmd, correlationId)
        );
    }
}