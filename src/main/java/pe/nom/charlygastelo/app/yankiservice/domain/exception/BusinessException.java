package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
