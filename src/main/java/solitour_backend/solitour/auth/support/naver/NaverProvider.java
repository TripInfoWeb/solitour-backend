package solitour_backend.solitour.auth.support.naver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import solitour_backend.solitour.auth.support.naver.dto.NaverTokenResponse;

@Getter
@Component
public class NaverProvider {

    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final String accessTokenUrl;
    private final String userInfoUrl;
    private final String grantType;
    private final String refreshGrantType;
    private final String revokeGrantType;
    private final String serviceProvider;
    private final String state = UUID.randomUUID().toString();


    public NaverProvider(@Value("${oauth2.naver.client.id}") String clientId,
                         @Value("${oauth2.naver.client.secret}") String clientSecret,
                         @Value("${oauth2.naver.url.auth}") String authUrl,
                         @Value("${oauth2.naver.url.token}") String accessTokenUrl,
                         @Value("${oauth2.naver.url.userinfo}") String userInfoUrl,
                         @Value("${oauth2.naver.service-provider}") String serviceProvider,
                         @Value("${oauth2.naver.grant-type}") String grantType,
                         @Value("${oauth2.naver.refresh-grant-type}") String refreshGrantType,
                         @Value("${oauth2.naver.revoke-grant-type}") String revokeGrantType) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authUrl = authUrl;
        this.accessTokenUrl = accessTokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.serviceProvider = serviceProvider;
        this.grantType = grantType;
        this.refreshGrantType = refreshGrantType;
        this.revokeGrantType = revokeGrantType;
    }

    public String generateTokenUrl(String grantType, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", grantType);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("state", state);
        return authUrl + "?" + concatParams(params);
    }

    public String generateAuthUrl(String redirectUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUrl);
        params.put("state", state);
        return authUrl + "?" + concatParams(params);
    }

    private String concatParams(Map<String, String> params) {
        return params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    public String generateAccessTokenUrl(String code) {
        return generateTokenUrl("authorization_code", code);
    }
}
