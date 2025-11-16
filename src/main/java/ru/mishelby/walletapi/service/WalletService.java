package ru.mishelby.walletapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mishelby.walletapi.exception.WalletOperationException;
import ru.mishelby.walletapi.model.*;
import ru.mishelby.walletapi.model.enums.OperationType;
import ru.mishelby.walletapi.utils.RepositoryHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import static ru.mishelby.walletapi.model.enums.OperationType.DEPOSIT;
import static ru.mishelby.walletapi.model.enums.OperationType.WITHDRAW;


@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {
    private final RepositoryHelper repositoryHelper;

    @Transactional(readOnly = true)
    public WalletDto getBalance(UUID uuid) {
        var walletEntity = repositoryHelper.findWalletByID(uuid);
        var walletDto = new WalletDto(walletEntity.getBalance(), LocalDateTime.now());

        log.info("[INFO] Wallet dto: {}", walletDto);
        return walletDto;
    }

    @Transactional
    public WalletOperationResponse deposit(UUID walletID, DepositOperationRequest depositOperationRequest) {
        BigDecimal transferAmount = getDepositAmount(depositOperationRequest);
        return supply(DEPOSIT, transferAmount, () -> {
            var walletEntity = repositoryHelper.findWalletForUpdateByID(walletID);

            BigDecimal oldBalance = walletEntity.getBalance();
            walletEntity.setBalance(walletEntity.getBalance().add(transferAmount));

            return getWalletOperationResponse(oldBalance, walletEntity.getBalance(), DEPOSIT);
        });
    }

    @Transactional
    public WalletOperationResponse withdraw(UUID walletID, TransferOperationRequest transferOperationRequest) {
        BigDecimal transferAmount = getTransferAmount(transferOperationRequest);
        return supply(WITHDRAW, transferAmount, () -> {
            var walletEntityFrom = repositoryHelper.findWalletForUpdateByID(walletID);

            checkWalletFromBalance(walletID, walletEntityFrom, transferAmount);

            var walletEntityTo = repositoryHelper.findWalletForUpdateByID(
                    transferOperationRequest.walletIDTo());

            BigDecimal oldBalance = walletEntityFrom.getBalance();

            walletEntityFrom.setBalance(walletEntityFrom.getBalance().subtract(transferAmount));
            walletEntityTo.setBalance(walletEntityTo.getBalance().add(transferAmount));

            return getWalletOperationResponse(oldBalance, walletEntityFrom.getBalance(), WITHDRAW);
        });
    }

    private static void checkWalletFromBalance(UUID walletID,
                                               WalletEntity walletEntityFrom,
                                               BigDecimal transferAmount) {
        if (walletEntityFrom.getBalance().compareTo(transferAmount) < 0) {
            log.error("[ERROR] Not enough balance!");
            throw new WalletOperationException("Not enough balance! Wallet ID %s"
                    .formatted(walletID)
            );
        }
    }

    private static WalletOperationResponse supply(OperationType operation,
                                                  BigDecimal amount,
                                                  Supplier<WalletOperationResponse> supplier) {
        log.info("[INFO] Request for wallet operation {}", operation);

        if (amount.signum() < 0) {
            log.error("[ERROR] Amount is negative!");
            throw new WalletOperationException("Incorrect amount");
        }

        return supplier.get();
    }

    private static BigDecimal getTransferAmount(TransferOperationRequest transfer) {
        return transfer.amount();
    }

    private static BigDecimal getDepositAmount(DepositOperationRequest deposit) {
        return deposit.amount();
    }

    private static WalletOperationResponse getWalletOperationResponse(
            BigDecimal oldBalance,
            BigDecimal newBalance,
            OperationType operationType) {
        return WalletOperationResponse.builder()
                .oldBalanceFrom(oldBalance)
                .newBalanceFrom(newBalance)
                .operationTime(LocalDateTime.now())
                .operationType(operationType)
                .build();
    }
}
