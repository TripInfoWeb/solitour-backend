package solitour_backend.solitour.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;

@RequiredArgsConstructor
public class TokenResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(AuthenticationPrincipal.class)) {
            return true;
        } else {
            return parameter.hasParameterAnnotation(AuthenticationRefreshPrincipal.class);
        }
    }

    @Override
    public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = "";
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (parameter.hasParameterAnnotation(AuthenticationPrincipal.class)) {
            token = CookieExtractor.findToken("access_token", request.getCookies());
        } else {
            token = CookieExtractor.findToken("refresh_token", request.getCookies());
        }

        return jwtTokenProvider.getPayload(token);
    }

}
