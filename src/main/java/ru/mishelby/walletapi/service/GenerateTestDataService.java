package ru.mishelby.walletapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mishelby.walletapi.model.WalletEntity;
import ru.mishelby.walletapi.repository.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Сервис для генерации тестовых данных в базе данных.
 * <p>
 * Создаёт тестовые кошельки со случайным балансом и датами, если таблица кошельков пуста.
 * <p>
 * Генерация выполняется автоматически при старте приложения, если включена соответствующая
 * настройка в {@code application.properties}:
 * <pre>
 * preload.test-data=true
 * </pre>
 * <p>
 * Реализует {@link org.springframework.boot.CommandLineRunner}, поэтому метод {@link #run(String...)}
 * вызывается после запуска Spring контекста.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "preload", name = "test-data", havingValue = "true")
public class GenerateTestDataService implements CommandLineRunner, PreInsertEventListener {
    private static final Random RANDOM = new  Random();

    private final WalletRepository walletRepository;

    @Value("${preload.walletID}")
    private UUID walletID;

    /**
     * Метод запускается после старта приложения и вызывает генерацию тестовых кошельков.
     *
     * @param args аргументы командной строки (не используются)
     * @throws Exception если при генерации данных произошла ошибка
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        generateWallets();
    }

    /**
     * Генерирует 50 тестовых кошельков со случайными параметрами:
     * <ul>
     *     <li>Баланс: случайное значение от 100 до 5000</li>
     *     <li>Дата создания: текущий момент</li>
     *     <li>Срок действия: текущий месяц + случайное количество месяцев до 24</li>
     * </ul>
     * <p>
     * Генерация выполняется только если таблица кошельков пуста.
     */
    public void generateWallets() {
        List<WalletEntity> allWallets = walletRepository.findAll();
        if (allWallets.isEmpty()) {
            for (int i = 0; i < 50; i++) {
                var walletEntity = new WalletEntity();
                walletEntity.setBalance(BigDecimal.valueOf(RANDOM.nextInt(100, 5000)));
                walletEntity.setCreatedAt(LocalDateTime.now());
                walletEntity.setExpirationDate(YearMonth.now().plusMonths(RANDOM.nextInt(24)));
                walletRepository.save(walletEntity);
            }
        }
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        setWalletID(event.getEntity());
        return false;
    }

    private void setWalletID(Object entity){
        if(entity instanceof WalletEntity walletEntity){
            walletEntity.setId(walletID);
        }
    }
}
