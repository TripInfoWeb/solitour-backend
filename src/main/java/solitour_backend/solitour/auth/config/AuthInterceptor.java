package solitour_backend.solitour.auth.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import solitour_backend.solitour.auth.exception.TokenNotExistsException;
import solitour_backend.solitour.auth.exception.TokenNotValidException;
import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        Optional<Authenticated> authenticated = parseAnnotation((HandlerMethod) handler,
                Authenticated.class);
        if (authenticated.isPresent()) {
            try {
                validateToken(request);
            }catch (TokenNotValidException e) {
                throw new TokenNotExistsException("토큰이 존재하지 않습니다.");
            }
        }
        return true;
    }

    private <T extends Annotation> Optional<T> parseAnnotation(HandlerMethod handler, Class<T> clazz) {
        T methodAnnotation = handler.getMethodAnnotation(clazz);

        if (methodAnnotation == null) {
            methodAnnotation = handler.getBeanType().getAnnotation(clazz);
        }

        return Optional.ofNullable(methodAnnotation);
    }

    private void validateToken(HttpServletRequest request) {
        String token = CookieExtractor.findToken("access_token", request.getCookies());
        if (jwtTokenProvider.validateTokenNotUsable(token)) {
            throw new TokenNotValidException("토큰이 유효하지 않습니다.");
        }
    }

}
