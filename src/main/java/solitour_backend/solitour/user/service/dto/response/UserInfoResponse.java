package solitour_backend.solitour.user.service.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user_image.entity.UserImage;

@Getter
public class UserInfoResponse {

    private final Long id;
    private final String userStatus;
    private final UserImage userImage;
    private final String nickname;
    private final Integer age;
    private final String sex;
    private final String email;
    private final String provider;
    private final String phoneNumber;
    private final LocalDateTime createdAt;
    private final Boolean isAdmin;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.userStatus = user.getUserStatus().getName();
        this.userImage = user.getUserImage();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.sex = user.getSex();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.phoneNumber = user.getPhoneNumber();
        this.createdAt = user.getCreatedAt();
        this.isAdmin = user.getIsAdmin();
    }
}
