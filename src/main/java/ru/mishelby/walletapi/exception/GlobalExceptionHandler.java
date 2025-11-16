package ru.mishelby.walletapi.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = getErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = getErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {

        if (nonNull(ex.getRequiredType()) && ex.getRequiredType().equals(java.util.UUID.class)) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .title("Invalid UUID")
                    .details("The provided UUID is invalid: " + ex.getValue())
                    .instance(request.getRequestURI())
                    .localDateTime(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Type Mismatch")
                .details(ex.getMessage())
                .instance(request.getRequestURI())
                .localDateTime(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private static ErrorResponse getErrorResponse(
            HttpStatus status,
            String title,
            String detail,
            String instance
    ) {
        return ErrorResponse.builder()
                .status(status.value())
                .title(title)
                .instance(instance)
                .details(detail)
                .localDateTime(LocalDateTime.now())
                .build();
    }
}
