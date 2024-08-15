package solitour_backend.solitour.auth.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.config.AuthenticationRefreshPrincipal;
import solitour_backend.solitour.auth.service.OauthService;
import solitour_backend.solitour.auth.service.dto.response.AccessTokenResponse;
import solitour_backend.solitour.auth.service.dto.response.LoginResponse;
import solitour_backend.solitour.auth.service.dto.response.OauthLinkResponse;


@RequiredArgsConstructor
@RequestMapping("/api/auth/oauth2")
@RestController
public class OauthController {

    private final OauthService oauthService;

    @GetMapping(value = "/login", params = {"type", "redirectUrl"})
    public ResponseEntity<OauthLinkResponse> access(@RequestParam String type, @RequestParam String redirectUrl) {
        OauthLinkResponse response = oauthService.generateAuthUrl(type, redirectUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/login", params = {"type", "code", "redirectUrl"})
    public ResponseEntity<LoginResponse> login(HttpServletResponse response, @RequestParam String type,
                                               @RequestParam String code, @RequestParam String redirectUrl) {
        LoginResponse loginResponse = oauthService.requestAccessToken(type, code, redirectUrl);

        String accessCookieHeader = setCookieHeader(loginResponse.getAccessToken());
        String refreshCookieHeader = setCookieHeader(loginResponse.getRefreshToken());

        response.addHeader("Set-Cookie", accessCookieHeader);
        response.addHeader("Set-Cookie", refreshCookieHeader);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, @AuthenticationPrincipal Long memberId) {
        oauthService.logout(response,memberId);

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

    private String setCookieHeader(Cookie cookie) {
        return String.format("%s=%s; Path=%s; Max-Age=%d;Secure; HttpOnly; SameSite=Lax",
                cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getMaxAge());
    }
}
