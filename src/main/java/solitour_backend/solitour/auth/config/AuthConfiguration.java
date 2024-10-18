package solitour_backend.solitour.auth.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.auth.support.JwtTokenProvider;

@RequiredArgsConstructor
@Configuration
public class AuthConfiguration implements WebMvcConfigurer {


    @Autowired
    private CorsProperties corsProperties;

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
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
                .allowCredentials(true);
    }
}
