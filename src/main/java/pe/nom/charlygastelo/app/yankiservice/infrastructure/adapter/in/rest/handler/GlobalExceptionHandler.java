package pe.nom.charlygastelo.app.yankiservice.infrastructure.adapter.in.rest.handler;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pe.nom.charlygastelo.app.yankiservice.domain.exception.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            WalletNotFoundException.class,
            WalletLinkNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleWalletNotFound(Exception ex) {
        log.error("Wallet or link not found: {}", ex.getMessage());

        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            WalletAlreadyExistsException.class,
            InvalidDebitCardException.class,
            InvalidYankiPaymentException.class,
            DebitCardNotLinkedException.class,
            InsufficientWalletBalanceException.class,
            WalletInactiveException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException ex) {
        log.warn("Business validation failed: {}", ex.getMessage());

        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid request value: {}", ex.getMessage());

        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred"
        );
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message
                ));
    }

    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message
    ) {
    }
}