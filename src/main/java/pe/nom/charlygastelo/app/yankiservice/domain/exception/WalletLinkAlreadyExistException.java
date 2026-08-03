package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class WalletLinkAlreadyExistException extends RuntimeException {
    public WalletLinkAlreadyExistException(String message) {
        super(message);
    }
}
