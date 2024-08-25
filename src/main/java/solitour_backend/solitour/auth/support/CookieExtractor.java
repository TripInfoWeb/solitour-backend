package solitour_backend.solitour.auth.support;

import jakarta.servlet.http.Cookie;

public class CookieExtractor {

    public static String findToken(String token, Cookie[] cookies) {
        String value = null;
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (token.equals(cookie.getName())) {
                value = cookie.getValue();
                break;
            }
        }
        return value;
    }
}
