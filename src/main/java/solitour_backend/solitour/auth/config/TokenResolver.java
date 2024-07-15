package solitour_backend.solitour.auth.config;

import jakarta.servlet.http.Cookie;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;

import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;

@RequiredArgsConstructor
public class TokenResolver implements HandlerMethodArgumentResolver {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
  }

  @Override
  public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
    String token = CookieExtractor.findToken("access_token",request.getCookies());

    return jwtTokenProvider.getPayload(token);
  }

}
