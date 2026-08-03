package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class DebitCardNotFoundException extends Exception{

    public DebitCardNotFoundException(String message) {
        super(message);
    }

    public DebitCardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
