package ru.mishelby.walletapi.utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mishelby.walletapi.model.WalletEntity;
import ru.mishelby.walletapi.repository.WalletRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryHelper {
    private final WalletRepository walletRepository;

    public WalletEntity findWalletByID(UUID walletID) {
        return walletRepository.findById(walletID).orElseThrow(
                () -> {
                    log.error("[ERROR] Wallet Not Found For UUID {}", walletID);
                    return new EntityNotFoundException("Wallet Not Found For UUID %s".formatted(walletID));
                }
        );
    }

    public WalletEntity findWalletForUpdateByID(UUID walletID) {
        return walletRepository.findByIdForUpdate(walletID).orElseThrow(
                () -> {
                    log.error("[ERROR] Wallet Not Found For UUID {}", walletID);
                    return new EntityNotFoundException("Wallet Not Found For UUID %s".formatted(walletID));
                }
        );
    }
}
