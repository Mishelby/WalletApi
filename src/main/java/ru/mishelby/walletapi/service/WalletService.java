package ru.mishelby.walletapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mishelby.walletapi.exception.WalletOperationException;
import ru.mishelby.walletapi.model.DepositOperationRequest;
import ru.mishelby.walletapi.model.TransferOperationRequest;
import ru.mishelby.walletapi.model.WalletDto;
import ru.mishelby.walletapi.model.WalletEntity;
import ru.mishelby.walletapi.model.WalletOperationResponse;
import ru.mishelby.walletapi.model.enums.OperationType;
import ru.mishelby.walletapi.utils.RepositoryHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import static ru.mishelby.walletapi.model.enums.OperationType.DEPOSIT;
import static ru.mishelby.walletapi.model.enums.OperationType.WITHDRAW;


/**
 * Сервис для работы с кошельками.
 * <p>
 * Поддерживает следующие операции:
 * <ul>
 *     <li>Получение баланса кошелька</li>
 *     <li>Пополнение кошелька (deposit)</li>
 *     <li>Снятие средств с кошелька (withdraw)</li>
 * </ul>
 * <p>
 * Использует {@link RepositoryHelper} для работы с базой данных и обеспечивает атомарность операций через аннотацию
 * {@link org.springframework.transaction.annotation.Transactional}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final RepositoryHelper repositoryHelper;

    /**
     * Получает текущий баланс кошелька.
     *
     * @param uuid UUID кошелька
     * @return {@link WalletDto} с балансом и текущим временем
     */
    @Transactional(readOnly = true)
    public WalletDto getBalance(UUID uuid) {
        var walletEntity = repositoryHelper.findWalletByID(uuid);
        var walletDto = new WalletDto(walletEntity.getBalance(), LocalDateTime.now());

        log.info("[INFO] Wallet dto: {}", walletDto);
        return walletDto;
    }

    /**
     * Пополняет баланс кошелька.
     *
     * @param walletID UUID кошелька
     * @param depositOperationRequest объект запроса с суммой для депозита
     * @return {@link WalletOperationResponse} с информацией об операции
     * @throws WalletOperationException если сумма отрицательная
     */
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

    /**
     * Снимает средства с кошелька и переводит их на другой кошелёк.
     *
     * @param walletID UUID кошелька-отправителя
     * @param transferOperationRequest объект запроса с суммой перевода и ID кошелька-получателя
     * @return {@link WalletOperationResponse} с информацией об операции
     * @throws WalletOperationException если сумма отрицательная или недостаточно средств на кошельке
     */
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

    /**
     * Проверяет, что на кошельке достаточно средств для перевода.
     *
     * @param walletID UUID кошелька
     * @param walletEntityFrom сущность кошелька
     * @param transferAmount сумма перевода
     * @throws WalletOperationException если баланс меньше суммы перевода
     */
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

    /**
     * Обёртка для операций с кошельком.
     * Проверяет корректность суммы и логирует запрос.
     *
     * @param operation тип операции
     * @param amount сумма операции
     * @param supplier поставщик результата операции
     * @return {@link WalletOperationResponse} результат операции
     * @throws WalletOperationException если сумма отрицательная
     */
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

    /**
     * Получает сумму для перевода из запроса.
     *
     * @param transfer объект запроса перевода
     * @return сумма перевода
     */
    private static BigDecimal getTransferAmount(TransferOperationRequest transfer) {
        return transfer.amount();
    }

    /**
     * Получает сумму депозита из запроса.
     *
     * @param deposit объект запроса депозита
     * @return сумма депозита
     */
    private static BigDecimal getDepositAmount(DepositOperationRequest deposit) {
        return deposit.amount();
    }

    /**
     * Формирует объект {@link WalletOperationResponse} после операции.
     *
     * @param oldBalance баланс до операции
     * @param newBalance баланс после операции
     * @param operationType тип операции
     * @return объект {@link WalletOperationResponse}
     */
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
