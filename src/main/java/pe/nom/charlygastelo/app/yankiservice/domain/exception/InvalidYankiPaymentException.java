package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class InvalidYankiPaymentException extends RuntimeException {

    public InvalidYankiPaymentException(String message) {
        super(message);
    }
}