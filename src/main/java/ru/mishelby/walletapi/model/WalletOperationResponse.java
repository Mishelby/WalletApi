package ru.mishelby.walletapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.mishelby.walletapi.model.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Schema(name = "WalletOperationResponse", description = "Получение информации кошелька после операции")
public record WalletOperationResponse(
        @Schema(name = "OperationType", description = "Тип операции (Пополнение/Перевод)")
        OperationType operationType,

        @Schema(name = "OldBalance", description = "Баланс до перевода")
        BigDecimal oldBalanceFrom,

        @Schema(name = "NewBalance", description = "Баланс после перевода")
        BigDecimal newBalanceFrom,

        @Schema(name = "OperationTime", description = "Время выполнения операции")
        LocalDateTime operationTime
) {

}
