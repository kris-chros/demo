package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching // Turns on Spring's caching infrastructure
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("productsCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Evict entries 10 minutes after they are written to the cache
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // Maximum number of entries this cache can hold
                .maximumSize(100));
        return cacheManager;
    }
}
