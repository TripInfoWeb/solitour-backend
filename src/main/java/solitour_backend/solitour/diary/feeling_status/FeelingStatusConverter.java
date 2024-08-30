package solitour_backend.solitour.diary.feeling_status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FeelingStatusConverter implements AttributeConverter<FeelingStatus, String> {

    @Override
    public String convertToDatabaseColumn(FeelingStatus userStatus) {
        return userStatus.getStatus();
    }

    @Override
    public FeelingStatus convertToEntityAttribute(String dbData) {
        return FeelingStatus.fromName(dbData);
    }
}
