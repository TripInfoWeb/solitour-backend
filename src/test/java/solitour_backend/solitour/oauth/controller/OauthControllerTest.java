package solitour_backend.solitour.oauth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import solitour_backend.solitour.auth.service.OauthService;
import solitour_backend.solitour.auth.service.dto.response.LoginResponse;
import solitour_backend.solitour.auth.service.dto.response.OauthLinkResponse;
import solitour_backend.solitour.user.user_status.UserStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private final String kakaoRedirectUrl = "http://localhost:8080/oauth2/callback/kakao";

    private final String kakaoOauthLink = "https://kauth.kakao.com/oauth/authorize?response_type=code&redirect_uri=http://localhost:8080/oauth2/callback/kakao&client_id=cleintId";

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

        BDDMockito.given(oauthService.requestAccessToken(anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
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
}