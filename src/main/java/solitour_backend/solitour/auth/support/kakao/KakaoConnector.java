package solitour_backend.solitour.auth.support.kakao;


import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
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
import solitour_backend.solitour.auth.support.kakao.dto.KakaoTokenResponse;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoUserResponse;

@Getter
@RequiredArgsConstructor
@Component
public class KakaoConnector {

    private static final String BEARER_TYPE = "Bearer";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final KakaoProvider provider;

    public ResponseEntity<KakaoUserResponse> requestKakaoUserInfo(String code, String redirectUrl) {
        String kakaoToken = requestAccessToken(code, redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.join(" ", BEARER_TYPE, kakaoToken));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return REST_TEMPLATE.exchange(provider.getUserInfoUrl(), HttpMethod.GET, entity,
                KakaoUserResponse.class);
    }

    public String requestAccessToken(String code, String redirectUrl) {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(
                createBody(code, redirectUrl), createHeaders());

        ResponseEntity<KakaoTokenResponse> response = REST_TEMPLATE.postForEntity(
                provider.getAccessTokenUrl(),
                entity, KakaoTokenResponse.class);

        return extractAccessToken(response);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private MultiValueMap<String, String> createBody(String code, String redirectUrl) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", provider.getClientId());
        body.add("client_secret", provider.getClientSecret());
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", provider.getGrantType());
        return body;
    }

    private String extractAccessToken(ResponseEntity<KakaoTokenResponse> responseEntity) {
        KakaoTokenResponse response = Optional.ofNullable(responseEntity.getBody())
                .orElseThrow(() -> new RuntimeException("카카오 토큰을 가져오는데 실패했습니다."));

        return response.getAccessToken();
    }

    public HttpStatusCode requestRevoke(String token) throws IOException {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(createRevokeHeaders(token));

        ResponseEntity<String> response = REST_TEMPLATE.postForEntity(provider.getRevokeUrl(), entity, String.class);

        return response.getStatusCode();
    }

    private HttpHeaders createRevokeHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", String.join(" ", BEARER_TYPE, token));
        return headers;
    }

}
