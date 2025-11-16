package ru.mishelby.walletapi.model;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferOperationRequest(
        UUID walletIDTo,
        BigDecimal amount
) {
}
