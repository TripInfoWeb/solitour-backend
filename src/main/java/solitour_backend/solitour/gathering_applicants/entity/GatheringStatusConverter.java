package solitour_backend.solitour.gathering_applicants.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GatheringStatusConverter implements AttributeConverter<GatheringStatus, String> {
    @Override
    public String convertToDatabaseColumn(GatheringStatus gatheringStatus) {
        return gatheringStatus.getName();
    }

    @Override
    public GatheringStatus convertToEntityAttribute(String dbData) {
        return GatheringStatus.fromName(dbData);
    }
}
