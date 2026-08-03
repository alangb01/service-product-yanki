package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(String message) {
        super(message);
    }
}