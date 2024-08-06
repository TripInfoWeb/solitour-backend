package solitour_backend.solitour.auth.support.google;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
import solitour_backend.solitour.auth.support.google.dto.GoogleTokenResponse;
import solitour_backend.solitour.auth.support.google.dto.GoogleUserResponse;

@Getter
@RequiredArgsConstructor
@Component
public class GoogleConnector {

    private static final String BEARER_TYPE = "Bearer";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final GoogleProvider provider;


    public ResponseEntity<GoogleUserResponse> requestGoogleUserInfo(String code, String redirectUrl) {
        String googleToken = requestAccessToken(code, redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.join(" ", BEARER_TYPE, googleToken));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return REST_TEMPLATE.exchange(provider.getUserInfoUrl(), HttpMethod.GET, entity,
                GoogleUserResponse.class);
    }

    public String requestAccessToken(String code, String redirectUrl) {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(
                createLoginBody(code, redirectUrl), createLoginHeaders());

        ResponseEntity<GoogleTokenResponse> response = REST_TEMPLATE.postForEntity(
                provider.getAccessTokenUrl(),
                entity, GoogleTokenResponse.class);

        return extractAccessToken(response);
    }

    public HttpStatusCode requestRevoke(String token) throws IOException {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(
                createLogoutBody(token), createLogoutHeaders());

        ResponseEntity<Void> response = REST_TEMPLATE.postForEntity(provider.getRevokeUrl(), entity, Void.class);

        return response.getStatusCode();
    }

    private HttpHeaders createLoginHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private MultiValueMap<String, String> createLoginBody(String code, String redirectUrl) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", provider.getClientId());
        body.add("client_secret", provider.getClientSecret());
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", provider.getGrantType());
        return body;
    }

    private HttpHeaders createLogoutHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> createLogoutBody(String token) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", token);
        return body;
    }

    private String extractAccessToken(ResponseEntity<GoogleTokenResponse> responseEntity) {
        GoogleTokenResponse response = Optional.ofNullable(responseEntity.getBody())
                .orElseThrow(() -> new RuntimeException("구글 토큰을 가져오는데 실패했습니다."));

        return response.getAccessToken();
    }

}
