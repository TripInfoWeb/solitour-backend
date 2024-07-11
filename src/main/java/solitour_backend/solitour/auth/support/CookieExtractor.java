package solitour_backend.solitour.auth.support;

import jakarta.servlet.http.Cookie;

public class CookieExtractor {
  public static String findToken(Cookie[] cookies) {
    String value = null;
    for (Cookie cookie : cookies) {
      if ("access_token".equals(cookie.getName())) {
        value = cookie.getValue();
        break;
      }
    }
    return value;
  }

}
