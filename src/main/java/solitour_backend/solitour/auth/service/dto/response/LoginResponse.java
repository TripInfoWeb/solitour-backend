package solitour_backend.solitour.auth.service.dto.response;

import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.user.user_status.UserStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LoginResponse {

    private Cookie accessToken;
    private Cookie refreshToken;
    private UserStatus loginStatus;
}
