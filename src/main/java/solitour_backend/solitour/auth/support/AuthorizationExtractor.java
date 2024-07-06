package solitour_backend.solitour.auth.support;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {

  private static final String AUTHORIZATION = "Authorization";
  private static final String ACCESS_TOKEN_TYPE =
      AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";
  private static final String BEARER_TYPE = "Bearer";

  public static String extract(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
    while (headers.hasMoreElements()) {
      String value = headers.nextElement();
      if ((value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
        String authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
        request.setAttribute(ACCESS_TOKEN_TYPE, value.substring(0, BEARER_TYPE.length()).trim());
        int commaIndex = authHeaderValue.indexOf(',');
        if (commaIndex > 0) {
          authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }
        return authHeaderValue;
      }
    }

    throw new RuntimeException("인증 헤더가 존재하지 않습니다.");
  }

  public static boolean hasNotAuthHeader(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
    return !headers.hasMoreElements();
  }
}
