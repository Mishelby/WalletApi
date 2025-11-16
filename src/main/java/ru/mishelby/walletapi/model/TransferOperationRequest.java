package ru.mishelby.walletapi.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferOperationRequest(
        @NotNull(message = "Target wallet ID must not be null")
        UUID walletIDTo,

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {
}
