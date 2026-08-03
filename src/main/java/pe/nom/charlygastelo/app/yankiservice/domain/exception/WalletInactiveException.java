package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class WalletInactiveException extends RuntimeException {

    public WalletInactiveException(String message) {
        super(message);
    }
}