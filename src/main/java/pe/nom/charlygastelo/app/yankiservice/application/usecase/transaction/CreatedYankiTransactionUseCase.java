package pe.nom.charlygastelo.app.yankiservice.application.usecase.transaction;
//
//import io.reactivex.rxjava3.core.Completable;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import pe.nom.charlygastelo.app.yankiservice.application.usecase.LinkDebitCardUseCase;
//import pe.nom.charlygastelo.app.yankiservice.domain.model.Transaction;
//import pe.nom.charlygastelo.app.yankiservice.domain.model.TransactionType;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CreatedYankiTransactionUseCase {
//    private final PaymentUseCase paymentUseCase;
//    private final LinkDebitCardUseCase linkDebitCardUseCase;
//
//    public boolean isYankiServiceResponsible(TransactionType type) {
//        return switch (type) {
//            case YANKI_PAYMENT,
//                 YANKI_LINK_DEBIT_CARD -> true;
//            default -> false;
//        };
//    }
//
//    public Completable execute(Transaction tx) {
//        log.info("[YANKI] Routing txId={} type={} to correct use case", tx.id(), tx.type());
//
//        return switch (tx.type()) {
//            case YANKI_PAYMENT -> paymentUseCase.execute(tx);
//
//            case YANKI_LINK_DEBIT_CARD -> linkDebitCardUseCase.execute(tx);
//
//            default -> {
//                log.warn("[YANKI] txId={} type={} ignored (not yanki responsibility)", tx.id(), tx.type());
//                yield Completable.complete();
//            }
//        };
//    }
//}
