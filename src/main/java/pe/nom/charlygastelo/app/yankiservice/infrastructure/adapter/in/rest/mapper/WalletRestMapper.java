package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

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
        LocalDateTime now = LocalDateTime.now();

        return new Wallet(
                null,
                DocumentType.valueOf(request.documentType()),
                request.documentNumber(),
                request.phone(),
                request.imei(),
                request.email(),
                null,
                BigDecimal.ZERO,
                WalletStatus.ACTIVE,
                now,
                now
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
                existing.debitCardId(),
                existing.balance(),
                WalletStatus.valueOf(request.status()),
                existing.createdAt(),
                LocalDateTime.now()
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
                wallet.debitCardId(),
                wallet.balance(),
                wallet.status().name(),
                wallet.createdAt(),
                wallet.updatedAt()
        );
    }

    public WalletBalanceResponse toBalanceResponse(Wallet wallet) {
        return new WalletBalanceResponse(
                wallet.id(),
                wallet.phone(),
                wallet.balance()
        );
    }
}