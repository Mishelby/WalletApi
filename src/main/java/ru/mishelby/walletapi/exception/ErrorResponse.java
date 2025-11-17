package ru.mishelby.walletapi.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(
        String title,

        int status,

        String details,

        String instance,

        LocalDateTime localDateTime,

        List<FieldError> fieldError
) {
    public record FieldError(
            String field,

            String message

    ) {}
}

