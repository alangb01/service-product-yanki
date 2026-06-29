package pe.nom.charlygastelo.app.yankiservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.nom.charlygastelo.app.yankiservice.application.usecase.CreateWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.DeleteWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.GetWalletBalanceUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.GetWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.LinkDebitCardUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.ListWalletsUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.SendYankiPaymentUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.UnlinkDebitCardUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.UpdateWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.domain.port.CardEventPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.MovementEventPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletEventProducerPort;
import pe.nom.charlygastelo.app.yankiservice.domain.port.WalletRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.mapper.WalletPersistentMapper;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.ReactiveWalletRepository;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.WalletRepositoryAdapter;

@Configuration
public class BeanConfig {

    @Bean
    public WalletRepositoryPort walletRepositoryPort(
            ReactiveWalletRepository repository,
            WalletPersistentMapper mapper) {

        return new WalletRepositoryAdapter(repository, mapper);
    }

    @Bean
    public CreateWalletUseCase createWalletUseCase(
            WalletRepositoryPort repository,
            WalletEventProducerPort producer) {

        return new CreateWalletUseCase(repository, producer);
    }

    @Bean
    public GetWalletUseCase getWalletUseCase(
            WalletRepositoryPort repository) {

        return new GetWalletUseCase(repository);
    }

    @Bean
    public ListWalletsUseCase listWalletsUseCase(
            WalletRepositoryPort repository) {

        return new ListWalletsUseCase(repository);
    }

    @Bean
    public UpdateWalletUseCase updateWalletUseCase(
            WalletRepositoryPort repository,
            WalletEventProducerPort producer) {

        return new UpdateWalletUseCase(repository, producer);
    }

    @Bean
    public DeleteWalletUseCase deleteWalletUseCase(
            WalletRepositoryPort repository,
            WalletEventProducerPort producer) {

        return new DeleteWalletUseCase(repository, producer);
    }

    @Bean
    public LinkDebitCardUseCase linkDebitCardUseCase(
            WalletRepositoryPort repository,
            CardEventPort cardEventPort,
            WalletEventProducerPort producer) {

        return new LinkDebitCardUseCase(repository, cardEventPort, producer);
    }

    @Bean
    public UnlinkDebitCardUseCase unlinkDebitCardUseCase(
            WalletRepositoryPort repository,
            WalletEventProducerPort producer) {

        return new UnlinkDebitCardUseCase(repository, producer);
    }

    @Bean
    public SendYankiPaymentUseCase sendYankiPaymentUseCase(
            WalletRepositoryPort repository,
            MovementEventPort movementEventPort,
            WalletEventProducerPort producer) {

        return new SendYankiPaymentUseCase(repository, movementEventPort, producer);
    }

    @Bean
    public GetWalletBalanceUseCase getWalletBalanceUseCase(
            WalletRepositoryPort repository) {

        return new GetWalletBalanceUseCase(repository);
    }
}