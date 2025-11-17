package ru.mishelby.walletapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WalletDto(
        UUID walletID,
        BigDecimal balance,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime requestedAt
) {
}
