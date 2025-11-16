package ru.mishelby.walletapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.mishelby.walletapi.model.converter.YearMonthConverter;
import ru.mishelby.walletapi.utils.FutureOrPresentYearMonth;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletEntity {
    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "expiration_date", nullable = false)
    @Convert(converter = YearMonthConverter.class)
    @FutureOrPresentYearMonth
    private YearMonth expirationDate;
}
