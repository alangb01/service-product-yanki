package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Account;
import pe.nom.charlygastelo.app.yankiservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.domain.model.WalletStatus;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.CreateWalletRequest;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.UpdateWalletRequest;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.WalletBalanceResponse;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.WalletResponse;

@Component
public class WalletRestMapper {

    public Wallet toDomain(CreateWalletRequest request) {
        Instant now = Instant.now();

        return new Wallet(
                null,
                DocumentType.valueOf(request.documentType()),
                request.documentNumber(),
                request.phone(),
                request.imei(),
                request.email(),
                WalletStatus.ACTIVE,
                now,
                null
        );
    }

    public Wallet toDomain(UpdateWalletRequest request, Wallet existing) {
        return new Wallet(
                existing.id(),
                existing.documentType(),
                existing.documentNumber(),
                existing.phone(),
                existing.imei(),
                request.email(),
                WalletStatus.valueOf(request.status()),
                existing.createdAt(),
                Instant.now()
        );
    }

    public WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.id(),
                wallet.documentType().name(),
                wallet.documentNumber(),
                wallet.phone(),
                wallet.imei(),
                wallet.email(),
                wallet.status().name(),
                formatInstant(wallet.createdAt()),
                formatInstant(wallet.updatedAt())
        );
    }

    public WalletBalanceResponse toBalanceResponse(Wallet wallet, Account account) {
        return new WalletBalanceResponse(
                wallet.id(),
                wallet.phone(),
                account.balance()
        );
    }

    public String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("America/Lima"));
        return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}