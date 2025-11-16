package ru.mishelby.walletapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "WalletDto", description = "Стандартное отображение информации кошелька")
public record WalletDto(
        @Schema(name = "Balance", description = "Новый баланс")
        BigDecimal balance,

        @Schema(name = "RequestedAt", description = "Дата запроса")
        LocalDateTime requestedAt
) {
}
