package ru.mishelby.walletapi.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

import static java.util.Objects.nonNull;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {
    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        if (nonNull(attribute)) {
            return attribute.toString();
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        if (nonNull(dbData)) {
            return YearMonth.parse(dbData);
        }
        return null;
    }
}
