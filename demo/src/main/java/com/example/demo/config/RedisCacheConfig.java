package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import java.time.Duration;
import java.util.Map;



@Slf4j
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {



    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        RedisCacheConfiguration base=RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                serializer  // ← use this, not new GenericJackson2JsonRedisSerializer()
                        )
                );
        Map<String,RedisCacheConfiguration> perCacheConfig=Map.of(
                "products",base.entryTtl(Duration.ofHours(24)),
                "revenue",base.entryTtl(Duration.ofHours(6)),
                "user_revenue",base.entryTtl(Duration.ofHours(6)),
                "stock_health",base.entryTtl(Duration.ofMinutes(20)),
                "status_order",base.entryTtl(Duration.ofMinutes(20)),
                "product_quantity",base.entryTtl(Duration.ofHours(20)),
                "Idempotency_key",base.entryTtl(Duration.ofHours(20))

        );

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(base.entryTtl(Duration.ofHours(24)))//fallback for the cache not configured
                .withInitialCacheConfigurations(perCacheConfig)
                .build();
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler(){
        return new SimpleCacheErrorHandler(){
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache,Object key){
                // Redis down on READ — log warning, Spring falls through to DB
                log.warn("Redis GET failed for key {}: {}", key, e.getMessage());
            }
            @Override
            public void handleCachePutError(RuntimeException e, Cache cache,
                                            Object key, Object value) {
                // Redis down on WRITE — log warning, proceed without caching
                log.warn("Redis PUT failed for key {}: {}", key, e.getMessage());
            }
            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache,
                                              Object key) {
                // Redis down on EVICT — log warning
                // This is riskier: the cache entry wasn't evicted
                // Stale data may be served until TTL expires
                log.warn("Redis EVICT failed for key {}: {}", key,
                        e.getMessage());
            }
        };
    }

}
