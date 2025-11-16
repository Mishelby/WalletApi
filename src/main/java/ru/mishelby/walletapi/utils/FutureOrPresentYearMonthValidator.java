package ru.mishelby.walletapi.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.YearMonth;

import static java.util.Objects.isNull;

public class FutureOrPresentYearMonthValidator implements ConstraintValidator<FutureOrPresentYearMonth, YearMonth> {
    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {
        if(isNull(value)) return false;
        return !value.isBefore(YearMonth.now());
    }
}
