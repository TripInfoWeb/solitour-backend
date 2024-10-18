package solitour_backend.solitour.oauth.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.*;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import solitour_backend.solitour.auth.entity.Token;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.auth.exception.UnsupportedLoginTypeException;
import solitour_backend.solitour.auth.service.OauthService;
import solitour_backend.solitour.auth.service.TokenService;
import solitour_backend.solitour.auth.service.dto.response.AccessTokenResponse;
import solitour_backend.solitour.auth.service.dto.response.LoginResponse;
import solitour_backend.solitour.auth.service.dto.response.OauthLinkResponse;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.auth.support.kakao.KakaoConnector;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.BlockedUserException;
import solitour_backend.solitour.user.exception.DeletedUserException;
import solitour_backend.solitour.user.exception.DormantUserException;
import solitour_backend.solitour.user.user_status.UserStatus;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
@Transactional
@SpringBootTest
class OauthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OauthService oauthService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private KakaoConnector kakaoConnector;

    private final String kakaoRedirectUrl = "http://localhost:8080/oauth2/callback/kakao";

    private final String kakaoOauthLink = "https://kauth.kakao.com/oauth/authorize?response_type=code&redirect_uri=http://localhost:8080/oauth2/callback/kakao&client_id=cleintId";

    @MockBean
    private TokenService tokenService;


    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(modifyUris(), prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("정상적으로 Oauth 인증 링크가 반환되는지 테스트한다")
    @Test
    void access() throws Exception {
        BDDMockito.given(oauthService.generateAuthUrl(eq("kakao"), anyString()))
                .willReturn(new OauthLinkResponse(kakaoOauthLink));

        mockMvc.perform(get("/api/auth/oauth2/login")
                        .queryParam("type", "kakao")
                        .queryParam("redirectUrl", kakaoRedirectUrl)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("oauthLink", notNullValue()))
                .andDo(
                        document("oauthLink",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @DisplayName("정상적으로 로그인될 시 토큰이 발급된다")
    @Test
    void login() throws Exception {
        Cookie accessCookie = new Cookie("access_token", "eyJhbGciOiJIUzI1NiJ9");
        Cookie refreshCookie = new Cookie("refresh_token", "l9Fp78G5l6RWbG9SMvOVnb0pnrEkWPHMPBmQw8c");

        BDDMockito.given(oauthService.requestAccessToken(anyString(), anyString(), anyString()))
                .willReturn(new LoginResponse(accessCookie, refreshCookie, UserStatus.PENDING));

        String code = "code";
        mockMvc.perform(get("/api/auth/oauth2/login")
                        .queryParam("code", code)
                        .queryParam("type", "kakao")
                        .queryParam("redirectUrl", kakaoRedirectUrl))
                .andExpectAll(
                        status().isOk(),
                        header().exists("Set-Cookie"),
                        jsonPath("$").value("PENDING"))
                .andDo(
                        document("oauthLogin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @DisplayName("재발급")
    @Test
    void reissueRefreshToken() throws Exception {
        Cookie accessCookie = new MockCookie("access_token", "accessToken");
        Cookie refreshCookie = new MockCookie("refresh_token", "refreshToken");
        passRefreshLogin(accessCookie);

        mockMvc.perform(post("/api/auth/oauth2/token/refresh")
                        .cookie(accessCookie)
                        .cookie(refreshCookie)
                )
                .andExpectAll(
                        status().isOk(),
                        header().exists("Set-Cookie")
                )
                .andDo(
                        document("oauthReissue",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @DisplayName("회원탈퇴")
    @Test
    void deleteUser() throws Exception {
        Cookie accessCookie = new MockCookie("access_token", "accessToken");
        Cookie refreshCookie = new MockCookie("refresh_token", "refreshToken");
        passLogin();
        BDDMockito.given(kakaoConnector.refreshToken(any()))
                .willReturn("refreshToken");

        doNothing().when(oauthService).revokeToken("kakao", "refreshToken");
        doNothing().when(oauthService).logout(any(), any());
        doNothing().when(oauthService).deleteUser(1L);

        mockMvc.perform(delete("/api/auth/oauth2")
                        .queryParam("type", "kakao")
                        .cookie(accessCookie)
                        .cookie(refreshCookie)
                )
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(
                        document("oauthDelete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }


    @DisplayName("Oauth 타입이 존재하지 않는경우 404코드가 반환된다")
    @Test
    void unsupportedLoginType() throws Exception {
        BDDMockito.given(oauthService.generateAuthUrl(argThat(provider ->
                        !provider.equals("google") && !provider.equals("kakao") && !provider.equals("naver")), anyString()))
                .willThrow(new UnsupportedLoginTypeException("지원하지 않는 oauth 로그인입니다."));

        mockMvc.perform(get("/api/auth/oauth2/login")
                        .queryParam("type", "facebook")
                        .queryParam("redirectUrl", "redirectUrl")
                )
                .andExpectAll(
                        status().isBadRequest());
    }

    @DisplayName("차단된 회원일 경우 403코드가 반환된다")
    @Test
    void blockedUser() throws Exception {
        BDDMockito.given(oauthService.requestAccessToken("kakao", "code", "redirectUrl"))
                .willThrow(new BlockedUserException("차단된 회원입니다."));

        mockMvc.perform(get("/api/auth/oauth2/login")
                        .queryParam("code", "code")
                        .queryParam("type", "kakao")
                        .queryParam("redirectUrl", "redirectUrl")
                )
                .andExpectAll(
                        status().isForbidden());
    }

    @DisplayName("탈퇴한 회원일 경우 403코드가 반환된다")
    @Test
    void dormantUser() throws Exception {
        BDDMockito.given(oauthService.requestAccessToken("kakao", "code", "redirectUrl"))
                .willThrow(new DeletedUserException("탈퇴된 회원입니다."));

        mockMvc.perform(get("/api/auth/oauth2/login")
                        .queryParam("code", "code")
                        .queryParam("type", "kakao")
                        .queryParam("redirectUrl", "redirectUrl")
                )
                .andExpectAll(
                        status().isForbidden());
    }

    @DisplayName("휴면 회원일 경우 423코드가 반환된다")
    @Test
    void lockedUser() throws Exception {
        BDDMockito.given(oauthService.requestAccessToken("kakao", "code", "redirectUrl"))
                .willThrow(new DormantUserException("휴면 회원입니다."));

        mockMvc.perform(get("/api/auth/oauth2/login")
                        .queryParam("code", "code")
                        .queryParam("type", "kakao")
                        .queryParam("redirectUrl", "redirectUrl")
                )
                .andExpectAll(
                        status().isLocked());
    }

    private void passRefreshLogin(Cookie accessCookie) {
        User user = User.builder()
                .oauthId("oauthId")
                .build();
        Token token = new Token(user, "refreshToken");

        BDDMockito.given(oauthService.reissueAccessToken(1L))
                .willReturn(new AccessTokenResponse(accessCookie));
        BDDMockito.given(jwtTokenProvider.validateTokenNotUsable(any()))
                .willReturn(false);
        BDDMockito.given(jwtTokenProvider.getPayload(any()))
                .willReturn(1L);
        BDDMockito.given(tokenRepository.findByUserId(1L))
                .willReturn(Optional.of(token));
    }

    private void passLogin() {
        User user = User.builder()
                .oauthId("oauthId")
                .build();
        Token token = new Token(user, "accessToken");

        BDDMockito.given(jwtTokenProvider.validateTokenNotUsable(any()))
                .willReturn(false);
        BDDMockito.given(jwtTokenProvider.getPayload(any()))
                .willReturn(1L);
        BDDMockito.given(tokenRepository.findByUserId(1L))
                .willReturn(Optional.of(token));
    }

}