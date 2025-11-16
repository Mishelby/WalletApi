package ru.mishelby.walletapi.utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mishelby.walletapi.model.WalletEntity;
import ru.mishelby.walletapi.repository.WalletRepository;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Вспомогательный сервис для централизованной работы с {@link WalletEntity}.
 * <p>
 * Содержит общие методы получения сущностей кошельков с единообразной
 * обработкой ошибок и логированием. Позволяет избежать дублирования
 * логики доступа к репозиторию в сервисах.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryHelper {
    private final WalletRepository walletRepository;

    /**
     * Получает сущность {@link WalletEntity} по её уникальному идентификатору.
     * <p>
     * Если кошелёк не найден, выбрасывается {@link EntityNotFoundException}.
     *
     * @param walletID UUID кошелька
     * @return сущность кошелька
     * @throws EntityNotFoundException если кошелёк не найден
     */
    public WalletEntity findWalletByID(UUID walletID) {
        return walletRepository.findById(walletID).orElseThrow(
                () -> {
                    log.error(defaultMessage(() -> "[ERROR] Wallet Not Found For UUID {}"), walletID);
                    return new EntityNotFoundException("Wallet Not Found For UUID %s".formatted(walletID));
                }
        );
    }

    /**
     * Получает сущность {@link WalletEntity} по её идентификатору,
     * одновременно устанавливая блокировку на запись в базе данных
     * (pessimistic write lock).
     * <p>
     * Используется в операциях изменения баланса для обеспечения
     * потокобезопасности при конкурентных обращениях.
     *
     * @param walletID UUID кошелька
     * @return сущность кошелька с установленной блокировкой
     * @throws EntityNotFoundException если кошелёк не найден
     */
    public WalletEntity findWalletForUpdateByID(UUID walletID) {
        return walletRepository.findByIdForUpdate(walletID).orElseThrow(
                () -> {
                    log.error(defaultMessage(() -> "[ERROR] Wallet Not Found For UUID {}"), walletID);
                    return new EntityNotFoundException("Wallet Not Found For UUID %s".formatted(walletID));
                }
        );
    }

    private static String defaultMessage(Supplier<String> messageSupplier) {
        return messageSupplier.get();
    }
}
