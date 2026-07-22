package pe.nom.charlygastelo.app.yankiservice.domain.exception;

public class ServiceTimeoutException extends  Exception{
    public ServiceTimeoutException(String message) {
        super(message);
    }

    public ServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
