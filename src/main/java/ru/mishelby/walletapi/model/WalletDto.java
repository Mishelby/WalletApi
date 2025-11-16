package ru.mishelby.walletapi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletDto(
        BigDecimal balance,
        LocalDateTime requestedAt
) {
}
