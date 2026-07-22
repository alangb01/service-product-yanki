package pe.nom.charlygastelo.app.yankiservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.nom.charlygastelo.app.yankiservice.domain.port.repository.WalletRepositoryPort;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.mapper.WalletPersistentMapper;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.reactive.WalletReactiveRepository;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.out.persistence.repository.WalletAdapter;

@Configuration
public class BeanConfig {

    @Bean
    public WalletRepositoryPort walletRepositoryPort(
            WalletReactiveRepository repository,
            WalletPersistentMapper mapper) {

        return new WalletAdapter(repository, mapper);
    }

}