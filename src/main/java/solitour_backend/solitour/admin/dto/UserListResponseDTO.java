package solitour_backend.solitour.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import solitour_backend.solitour.user.user_status.UserStatus;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDTO {

    private Long id;
    private UserStatus userStatus;
    private String oauthId;
    private String provider;
    private String nickname;
    private String name;
    private Integer age;
    private String sex;
    private String email;
    private String phoneNumber;
    private Boolean isAdmin;
    private LocalDateTime latestLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
