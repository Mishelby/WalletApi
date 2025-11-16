package ru.mishelby.walletapi.model;

import lombok.Builder;
import ru.mishelby.walletapi.model.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record WalletOperationResponse(
        OperationType operationType,
        BigDecimal oldBalanceFrom,
        BigDecimal newBalanceFrom,
        LocalDateTime operationTime
) {

}
