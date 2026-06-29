package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class InsufficientWalletBalanceException extends RuntimeException {

    public InsufficientWalletBalanceException(String message) {
        super(message);
    }
}