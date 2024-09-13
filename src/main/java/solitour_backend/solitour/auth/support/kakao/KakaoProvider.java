package solitour_backend.solitour.auth.support.kakao;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoProvider {

    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final String accessTokenUrl;
    private final String userInfoUrl;
    private final String grantType;
    private final String refreshGrantType;
    private final String revokeUrl;
    private final String scope;


    public KakaoProvider(@Value("${oauth2.kakao.client.id}") String clientId,
                         @Value("${oauth2.kakao.client.secret}") String clientSecret,
                         @Value("${oauth2.kakao.url.auth}") String authUrl,
                         @Value("${oauth2.kakao.url.token}") String accessTokenUrl,
                         @Value("${oauth2.kakao.url.userinfo}") String userInfoUrl,
                         @Value("${oauth2.kakao.grant-type}") String grantType,
                         @Value("${oauth2.kakao.refresh-grant-type}") String refreshGrantType,
                         @Value("${oauth2.kakao.url.revoke}") String revokeUrl,
                         @Value("${oauth2.kakao.scope}") String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authUrl = authUrl;
        this.accessTokenUrl = accessTokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.grantType = grantType;
        this.refreshGrantType = refreshGrantType;
        this.revokeUrl = revokeUrl;
        this.scope = scope;
    }

    public String generateAuthUrl(String redirectUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUrl);
        return authUrl + "?" + concatParams(params);
    }

    private String concatParams(Map<String, String> params) {
        return params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
