package solitour_backend.solitour.auth.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.config.AuthenticationRefreshPrincipal;
import solitour_backend.solitour.auth.entity.Token;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.auth.exception.TokenNotExistsException;
import solitour_backend.solitour.auth.exception.UnsupportedLoginTypeException;
import solitour_backend.solitour.auth.exception.UserRevokeErrorException;
import solitour_backend.solitour.auth.service.OauthService;
import solitour_backend.solitour.auth.service.dto.response.AccessTokenResponse;
import solitour_backend.solitour.auth.service.dto.response.LoginResponse;
import solitour_backend.solitour.auth.service.dto.response.OauthLinkResponse;
import solitour_backend.solitour.auth.support.google.GoogleConnector;
import solitour_backend.solitour.auth.support.kakao.KakaoConnector;
import solitour_backend.solitour.auth.support.kakao.dto.request.CreateUserInfoRequest;
import solitour_backend.solitour.auth.support.naver.NaverConnector;
import solitour_backend.solitour.user.user_status.UserStatus;


@RequiredArgsConstructor
@RequestMapping("/api/auth/oauth2")
@RestController
public class OauthController {

    private final OauthService oauthService;
    private final KakaoConnector kakaoConnector;
    private final GoogleConnector googleConnector;
    private final NaverConnector naverConnector;
    private final TokenRepository tokenRepository;

    @GetMapping(value = "/login", params = {"type", "redirectUrl"})
    public ResponseEntity<OauthLinkResponse> access(@RequestParam String type, @RequestParam String redirectUrl) {
        OauthLinkResponse response = oauthService.generateAuthUrl(type, redirectUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login/kakao", params = {"code", "redirectUrl"})
    public ResponseEntity<UserStatus> kakaoLogin(HttpServletResponse response,
                                                 @RequestParam String code, @RequestParam String redirectUrl,
                                                 @RequestBody CreateUserInfoRequest createUserInfoRequest) {
        LoginResponse loginResponse = oauthService.requestkakaoAccessToken(code, redirectUrl, createUserInfoRequest);

        String accessCookieHeader = setCookieHeader(loginResponse.getAccessToken());
        String refreshCookieHeader = setCookieHeader(loginResponse.getRefreshToken());

        response.addHeader("Set-Cookie", accessCookieHeader);
        response.addHeader("Set-Cookie", refreshCookieHeader);

        return ResponseEntity.ok(loginResponse.getLoginStatus());
    }


    @GetMapping(value = "/login", params = {"type", "code", "redirectUrl"})
    public ResponseEntity<UserStatus> login(HttpServletResponse response, @RequestParam String type,
                                            @RequestParam String code, @RequestParam String redirectUrl) {
        LoginResponse loginResponse = oauthService.requestAccessToken(type, code, redirectUrl);

        String accessCookieHeader = setCookieHeader(loginResponse.getAccessToken());
        String refreshCookieHeader = setCookieHeader(loginResponse.getRefreshToken());

        response.addHeader("Set-Cookie", accessCookieHeader);
        response.addHeader("Set-Cookie", refreshCookieHeader);

        return ResponseEntity.ok(loginResponse.getLoginStatus());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, @AuthenticationPrincipal Long memberId) {
        oauthService.logout(response, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Void> reissueAccessToken(HttpServletResponse response,
                                                   @AuthenticationRefreshPrincipal Long memberId) {
        AccessTokenResponse accessToken = oauthService.reissueAccessToken(memberId);

        String accessCookieHeader = setCookieHeader(accessToken.getAccessToken());
        response.addHeader("Set-Cookie", accessCookieHeader);

        return ResponseEntity.ok().build();
    }

    @Authenticated
    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(HttpServletResponse response, @AuthenticationPrincipal Long id,
                                           @RequestParam String type) {
        Token token = tokenRepository.findByUserId(id)
                .orElseThrow(() -> new TokenNotExistsException("토큰이 존재하지 않습니다"));
        String oauthRefreshToken = getOauthAccessToken(type, token.getOauthToken());

        try {
            oauthService.revokeToken(type, oauthRefreshToken);

            oauthService.logout(response, id);
            oauthService.deleteUser(id);
        } catch (Exception e) {
            throw new UserRevokeErrorException("회원 탈퇴 중 오류가 발생했습니다");
        }

        return ResponseEntity.noContent().build();
    }

    private String setCookieHeader(Cookie cookie) {
        return String.format("%s=%s; Path=%s; Max-Age=%d;Secure; HttpOnly; SameSite=Lax",
                cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getMaxAge());
    }

    private String getOauthAccessToken(String type, String refreshToken) {
        String token = "";
        switch (type) {
            case "kakao" -> {
                token = kakaoConnector.refreshToken(refreshToken);
            }
            case "naver" -> {
                token = naverConnector.refreshToken(refreshToken);
            }
//            case "google" -> {
//                token = googleConnector.refreshToken(refreshToken);
//            }
            default -> throw new UnsupportedLoginTypeException("지원하지 않는 로그인 타입입니다");
        }
        return token;
    }

}
