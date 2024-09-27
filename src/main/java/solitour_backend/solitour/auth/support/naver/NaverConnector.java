package solitour_backend.solitour.auth.support.naver;


import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoUserResponse;
import solitour_backend.solitour.auth.support.naver.dto.NaverTokenAndUserResponse;
import solitour_backend.solitour.auth.support.naver.dto.NaverTokenResponse;
import solitour_backend.solitour.auth.support.naver.dto.NaverUserResponse;

@Getter
@RequiredArgsConstructor
@Component
public class NaverConnector {

    private static final String BEARER_TYPE = "Bearer";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final NaverProvider provider;

    public NaverTokenAndUserResponse requestNaverUserInfo(String code) {
        NaverTokenResponse naverTokenResponse = requestAccessToken(code);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.join(" ", BEARER_TYPE, naverTokenResponse.getAccessToken()));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> responseEntity = REST_TEMPLATE.exchange(provider.getUserInfoUrl(),
                HttpMethod.GET, entity,
                NaverUserResponse.class);

        return new NaverTokenAndUserResponse(naverTokenResponse, responseEntity.getBody());

    }

    public NaverTokenResponse requestAccessToken(String code) {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(
                createBody(code), createHeaders());

        return REST_TEMPLATE.postForEntity(
                provider.getAccessTokenUrl(),
                entity,
                NaverTokenResponse.class).getBody();
    }

    public String refreshToken(String refreshToken) {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(
                createRefreshBody(refreshToken), createHeaders());

        return REST_TEMPLATE.postForEntity(
                provider.getAccessTokenUrl(),
                entity, NaverTokenResponse.class).getBody().getAccessToken();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private MultiValueMap<String, String> createBody(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("grant_type", provider.getGrantType());
        body.add("client_id", provider.getClientId());
        body.add("client_secret", provider.getClientSecret());
        body.add("state", UUID.randomUUID().toString());
        return body;
    }

    private MultiValueMap<String, String> createRefreshBody(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", provider.getRefreshGrantType());
        body.add("client_id", provider.getClientId());
        body.add("client_secret", provider.getClientSecret());
        body.add("refresh_token", refreshToken);
        return body;
    }

    public HttpStatusCode requestRevoke(String token) throws IOException {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(createRevokeBody(token),createRevokeHeaders(token));

        ResponseEntity<String> response = REST_TEMPLATE.postForEntity(provider.getAccessTokenUrl(), entity, String.class);

        return response.getStatusCode();
    }

    private MultiValueMap<String, String> createRevokeBody(String accessToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", provider.getClientId());
        body.add("client_secret", provider.getClientSecret());
        body.add("grant_type", provider.getRevokeGrantType());
        body.add("access_token", accessToken);
        body.add("service_provider", provider.getServiceProvider());
        return body;
    }

    private HttpHeaders createRevokeHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", String.join(" ", BEARER_TYPE, token));
        return headers;
    }
}
