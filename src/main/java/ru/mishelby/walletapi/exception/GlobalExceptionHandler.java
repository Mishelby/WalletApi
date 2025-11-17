package ru.mishelby.walletapi.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        var errorResponse = getErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException ex, HttpServletRequest request) {
        var errorResponse = getErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            BindException ex,
            HttpServletRequest request) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        String details = fieldErrors.stream()
                .map(ErrorResponse.FieldError::message)
                .collect(Collectors.joining("; "));

        var validationException = getErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation exeption",
                details,
                request.getRequestURI(),
                fieldErrors
        );

        return new ResponseEntity<>(validationException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class,
            WalletOperationException.class,
    })
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {

        if (nonNull(ex.getRequiredType()) && ex.getRequiredType().equals(java.util.UUID.class)) {
            var error = getErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Invalid UUID",
                    "The provided UUID is invalid: " + ex.getValue(),
                    request.getRequestURI(),
                    null
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        var error = getErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Type Mismatch",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private static ErrorResponse getErrorResponse(
            HttpStatus status,
            String title,
            String detail,
            String instance,
            List<ErrorResponse.FieldError> fieldErrors
    ) {
        return ErrorResponse.builder()
                .status(status.value())
                .title(title)
                .instance(instance)
                .details(detail)
                .fieldError(fieldErrors)
                .localDateTime(LocalDateTime.now())
                .build();
    }
}
