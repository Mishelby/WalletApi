package ru.mishelby.walletapi.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        String title,
        int status,
        String details,
        String instance,
        LocalDateTime localDateTime
) {
}
