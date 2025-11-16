package ru.mishelby.walletapi.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureOrPresentYearMonthValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentYearMonth {
    String message() default "must be in the present or future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
