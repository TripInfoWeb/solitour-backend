package solitour_backend.solitour.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.user.user_status.UserStatus;
import solitour_backend.solitour.user.user_status.UserStatusConverter;

import java.time.LocalDateTime;
import solitour_backend.solitour.user_image.UserImage;

@Entity
@Getter
@Builder
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_status_id")
    @Convert(converter = UserStatusConverter.class)
    private UserStatus userStatus;

    @Column(name = "user_oauth_id")
    private String oauthId;

    @Column(name = "provider")
    private String provider;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private UserImage userImage;

    @Column(name = "user_nickname")
    private String nickname;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_age")
    private Integer age;

    @Column(name = "user_sex")
    private String sex;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_phone_number")
    private String phoneNumber;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "user_latest_login_at")
    private LocalDateTime latestLoginAt;

    @Column(name = "user_created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_deleted_at")
    private LocalDateTime deletedAt;
}
