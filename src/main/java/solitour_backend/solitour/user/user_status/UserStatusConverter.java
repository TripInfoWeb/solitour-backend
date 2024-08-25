package solitour_backend.solitour.user.user_status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserStatus userStatus) {
        return userStatus.getName();
    }

    @Override
    public UserStatus convertToEntityAttribute(String dbData) {
        return UserStatus.fromName(dbData);
    }
}
