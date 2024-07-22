package solitour_backend.solitour.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.auth.support.JwtTokenProvider;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class AuthConfiguration implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(jwtTokenProvider))
            .addPathPatterns("/api/**");

        registry.addInterceptor(new RefreshTokenAuthInterceptor(jwtTokenProvider, tokenRepository))
            .addPathPatterns("/api/auth/oauth2/token/refresh");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*")
            .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TokenResolver(jwtTokenProvider));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final String LOCAL_URL = "http://localhost:3000";
        final String ALLOWED_URL = "https://solitour.ssssksss.xyz";

        registry.addMapping("/**")
            .allowedOrigins(LOCAL_URL, ALLOWED_URL)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
