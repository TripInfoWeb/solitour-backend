package solitour_backend.solitour.gathering.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AllowedSexConverter implements AttributeConverter<AllowedSex, String> {
    @Override
    public String convertToDatabaseColumn(AllowedSex allowedSex) {
        return allowedSex.getName();
    }

    @Override
    public AllowedSex convertToEntityAttribute(String dbData) {
        return AllowedSex.fromName(dbData);
    }
}
