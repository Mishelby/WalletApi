package ru.mishelby.walletapi.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mishelby.walletapi.model.WalletEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository
        extends JpaRepository<WalletEntity, UUID>, JpaSpecificationExecutor<WalletEntity> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT we
            FROM WalletEntity we
            WHERE we.id = :uuid
            """)
    Optional<WalletEntity> findByIdForUpdate(@Param("uuid") UUID walletID);
}