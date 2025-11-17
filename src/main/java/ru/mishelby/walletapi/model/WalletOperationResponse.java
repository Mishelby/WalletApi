package ru.mishelby.walletapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import ru.mishelby.walletapi.model.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record WalletOperationResponse(
        OperationType operationType,

        BigDecimal oldBalanceFrom,

        BigDecimal newBalanceFrom,

        LocalDateTime operationTime
) {

}
