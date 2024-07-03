package solitour_backend.solitour.auth.config;


import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import solitour_backend.solitour.auth.support.AuthorizationExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    if (CorsUtils.isPreFlightRequest(request)) {
      return true;
    }

    Optional<Authenticated> authenticated = parseAnnotation((HandlerMethod) handler,
        Authenticated.class);
    if (authenticated.isPresent()) {
      validateToken(request);
    }
    return true;
  }

  private <T extends Annotation> Optional<T> parseAnnotation(HandlerMethod handler,
      Class<T> clazz) {
    return Optional.ofNullable(handler.getMethodAnnotation(clazz));
  }

  private void validateToken(HttpServletRequest request) {
    String token = AuthorizationExtractor.extract(request);
    if (jwtTokenProvider.validateTokenNotUsable(token)) {
      throw new RuntimeException("토큰이 유효하지 않습니다.");
    }
  }
}