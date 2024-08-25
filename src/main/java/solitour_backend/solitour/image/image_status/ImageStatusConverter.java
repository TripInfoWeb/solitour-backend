package solitour_backend.solitour.image.image_status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ImageStatusConverter implements AttributeConverter<ImageStatus, String> {

    @Override
    public String convertToDatabaseColumn(ImageStatus imageStatus) {
        return imageStatus.getName();
    }

    @Override
    public ImageStatus convertToEntityAttribute(String dbData) {
        return ImageStatus.fromName(dbData);
    }
}
