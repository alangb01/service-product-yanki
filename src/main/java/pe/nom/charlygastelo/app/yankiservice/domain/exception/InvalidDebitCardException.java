package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class InvalidDebitCardException extends RuntimeException {

    public InvalidDebitCardException(String message) {
        super(message);
    }
}