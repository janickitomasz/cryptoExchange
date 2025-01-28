package tja.software.crypto.config;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${cache.currencyCache.ttl}")
    long currencyCacheTtl;

    @Value("${cache.ratesCache.ttl}")
    long ratesCacheTTl;

    @Value("${cache.controllerCache.ttl}")
    long controllerCacheTTl;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setAsyncCacheMode(true);
        cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(32000));

        return cacheManager;
    }

    @Bean
    public AsyncCache<Object, Object> currencyCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(currencyCacheTtl, TimeUnit.SECONDS)
                .buildAsync();
    }

    @Bean
    public AsyncCache<Object, Object> ratesCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(ratesCacheTTl, TimeUnit.SECONDS)
                .buildAsync();
    }

    @Bean
    public AsyncCache<Object, Object> controllerCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(controllerCacheTTl, TimeUnit.SECONDS)
                .buildAsync();
    }
}
