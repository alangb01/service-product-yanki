package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletLinkCommand;
import pe.nom.charlygastelo.app.yankiservice.application.command.WalletPaymentCommand;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.*;
import pe.nom.charlygastelo.app.yankiservice.application.usecase.transaction.PaymentUseCase;
import pe.nom.charlygastelo.app.yankiservice.domain.model.Wallet;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.dto.*;
import pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.mapper.WalletRestMapper;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallets")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletUseCase getWalletUseCase;
    private final ListWalletsUseCase listWalletsUseCase;
    private final UpdateWalletUseCase updateWalletUseCase;
    private final DeleteWalletUseCase deleteWalletUseCase;
    private final LinkDebitCardUseCase linkDebitCardUseCase;
    private final UnlinkDebitCardUseCase unlinkDebitCardUseCase;
    private final PaymentUseCase paymentUseCase;
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

    @GetMapping("/by-phone/{phoneNumber}")
    public Single<ResponseEntity<WalletResponse>> getByPhone(
            @PathVariable String phoneNumber) {

        return getWalletUseCase.byPhone(phoneNumber)
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

    @PostMapping("/{id}/link-debit-card")
    public Single<ResponseEntity<WalletResponse>> linkDebitCard(
            @PathVariable String id,
            @Valid @RequestBody LinkDebitCardRequest request) {

        WalletLinkCommand walletLinkCommand = new WalletLinkCommand(id, request.cardId());
        return linkDebitCardUseCase.execute(walletLinkCommand)
                .map(result ->
                        ResponseEntity.ok(mapper.toResponse(result.wallet()))
                );
    }

    @PostMapping("/{id}/unlink-debit-card")
    public Single<ResponseEntity<WalletResponse>> unlinkDebitCard(
            @PathVariable String id) {


        return unlinkDebitCardUseCase.execute(id)
                .map(wallet ->
                        ResponseEntity.ok().build()
                );
    }

    @PostMapping("/{id}/payments/send")
    public Single<ResponseEntity<Void>> sendPayment(
            @PathVariable String id,
            @Valid @RequestBody WalletPaymentRequest request) {

        WalletPaymentCommand paymentCmd = new WalletPaymentCommand(
                id,
                request.customerId(),
                request.sourcePhone(),
                request.targetPhone(),
                request.amount(),
                request.description()
            );
        return paymentUseCase.execute(paymentCmd)
                .map(result -> ResponseEntity.ok().build());
    }

    @GetMapping("/{id}/balance")
    public Single<ResponseEntity<WalletBalanceResponse>> balance(
            @PathVariable String id) {

        return getWalletBalanceUseCase.execute(id)
                .map(wallet ->
                        ResponseEntity.ok(mapper.toBalanceResponse(wallet.wallet(), wallet.account()))
                );
    }
}