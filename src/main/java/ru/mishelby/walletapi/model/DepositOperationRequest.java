package ru.mishelby.walletapi.model;

import java.math.BigDecimal;

public record DepositOperationRequest(
        BigDecimal amount
) {
}
