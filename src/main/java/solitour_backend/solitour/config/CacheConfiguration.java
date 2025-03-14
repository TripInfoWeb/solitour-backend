package solitour_backend.solitour.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("RankReadMapper.findRank");
        cacheManager.setCaffeine(caffeineCacheConfig());
        return cacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineCacheConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(100) // 초기 캐시 용량
                .maximumSize(1000) // 최대 캐시 용량
                .expireAfterWrite(1, TimeUnit.MINUTES) // 캐시 만료 시간
                .recordStats(); // 통계 수집 활성화
    }
}