package ru.mishelby.walletapi.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(name = "ErrorResponse", description = "Стандартная структура ошибки")
public record ErrorResponse(
        @Schema(description = "Краткое название ошибки", example = "Invalid UUID")
        String title,

        @Schema(description = "HTTP статус", example = "400")
        int status,

        @Schema(description = "Подробности ошибки", example = "The provided UUID is invalid: 123")
        String details,

        @Schema(description = "URI запроса", example = "/api/v1/wallets/123")
        String instance,

        @Schema(description = "Время ошибки", example = "2025-11-16T21:10:00")
        LocalDateTime localDateTime,

        @Schema(description = "Поля валидации", example = "{field : amount, message : must be greater than zero}")
        List<FieldError> fieldError
) {
    @Schema(description = "Ошибка конкретного поля")
    public record FieldError(String field, String message) {
        public String getMessage() {
            return message;
        }
    }
}
