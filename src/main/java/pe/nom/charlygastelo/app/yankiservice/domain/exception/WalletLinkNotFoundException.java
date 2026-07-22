package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class WalletLinkNotFoundException extends Exception{

    public WalletLinkNotFoundException(String message) {
        super(message);
    }

    public WalletLinkNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
