package solitour_backend.solitour.auth.service.dto.response;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessTokenResponse {

    private final Cookie accessToken;
}
