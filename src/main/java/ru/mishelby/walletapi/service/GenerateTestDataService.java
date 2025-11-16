package ru.mishelby.walletapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "preload", name = "test-data", havingValue = "true")
public class GenerateTestDataService implements CommandLineRunner {
    private final WalletRepository walletRepository;
    private static final Random RANDOM = new  Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        generateWallets();
    }

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

}
