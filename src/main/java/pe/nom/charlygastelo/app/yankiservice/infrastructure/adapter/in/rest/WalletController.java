package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.nom.charlygastelo.app.yankiservice.application.usecase.CreateWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.DeleteWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.GetWalletBalanceUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.GetWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.LinkDebitCardUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.ListWalletsUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.SendYankiPaymentUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.UnlinkDebitCardUseCase;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.UpdateWalletUseCase;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.CreateWalletRequest;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.LinkDebitCardRequest;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.SendYankiPaymentRequest;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.UpdateWalletRequest;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.WalletBalanceResponse;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.WalletResponse;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.mapper.WalletRestMapper;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletUseCase getWalletUseCase;
    private final ListWalletsUseCase listWalletsUseCase;
    private final UpdateWalletUseCase updateWalletUseCase;
    private final DeleteWalletUseCase deleteWalletUseCase;
    private final LinkDebitCardUseCase linkDebitCardUseCase;
    private final UnlinkDebitCardUseCase unlinkDebitCardUseCase;
    private final SendYankiPaymentUseCase sendYankiPaymentUseCase;
    private final GetWalletBalanceUseCase getWalletBalanceUseCase;
    private final WalletRestMapper mapper;

    @PostMapping
    public Single<ResponseEntity<WalletResponse>> create(
            @Valid @RequestBody CreateWalletRequest request) {

        Wallet wallet = mapper.toDomain(request);

        return createWalletUseCase.execute(wallet)
                .map(saved ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(mapper.toResponse(saved))
                );
    }

    @GetMapping
    public Flowable<WalletResponse> list() {
        return listWalletsUseCase.execute()
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public Single<ResponseEntity<WalletResponse>> getById(
            @PathVariable String id) {

        return getWalletUseCase.byId(id)
                .map(wallet ->
                        ResponseEntity.ok(mapper.toResponse(wallet))
                )
                .toSingle();
    }

    @GetMapping("/phone/{phone}")
    public Single<ResponseEntity<WalletResponse>> getByPhone(
            @PathVariable String phone) {

        return getWalletUseCase.byPhone(phone)
                .map(wallet ->
                        ResponseEntity.ok(mapper.toResponse(wallet))
                )
                .toSingle();
    }

    @PutMapping("/{id}")
    public Single<ResponseEntity<WalletResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateWalletRequest request) {

        return getWalletUseCase.byId(id)
                .flatMapSingle(existing -> {
                    Wallet wallet = mapper.toDomain(request, existing);
                    return updateWalletUseCase.execute(id, wallet);
                })
                .map(updated ->
                        ResponseEntity.ok(mapper.toResponse(updated))
                )
                .toSingle();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Completable delete(@PathVariable String id) {
        return deleteWalletUseCase.execute(id);
    }

    @PatchMapping("/{walletId}/link-debit-card")
    public Single<ResponseEntity<WalletResponse>> linkDebitCard(
            @PathVariable String walletId,
            @Valid @RequestBody LinkDebitCardRequest request) {

        return linkDebitCardUseCase.execute(walletId, request.cardId())
                .map(wallet ->
                        ResponseEntity.ok(mapper.toResponse(wallet))
                );
    }

    @PatchMapping("/{walletId}/unlink-debit-card")
    public Single<ResponseEntity<WalletResponse>> unlinkDebitCard(
            @PathVariable String walletId) {

        return unlinkDebitCardUseCase.execute(walletId)
                .map(wallet ->
                        ResponseEntity.ok(mapper.toResponse(wallet))
                );
    }

    @PostMapping("/payments/send")
    public Completable sendPayment(
            @Valid @RequestBody SendYankiPaymentRequest request) {

        return sendYankiPaymentUseCase.execute(
                request.sourcePhone(),
                request.targetPhone(),
                request.amount(),
                request.description()
        );
    }

    @GetMapping("/{id}/balance")
    public Single<ResponseEntity<WalletBalanceResponse>> balance(
            @PathVariable String id) {

        return getWalletBalanceUseCase.execute(id)
                .map(wallet ->
                        ResponseEntity.ok(mapper.toBalanceResponse(wallet))
                );
    }
}