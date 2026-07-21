package com.example.demo.service;

import com.example.demo.entity.IdempotencyKey;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@Slf4j
public class IdempotencyRedisService {
    private CacheManager cacheManager;
    private IdempotencyService idempotencyService;
    IdempotencyRedisService(CacheManager cacheManager,IdempotencyService idempotencyService){
        this.cacheManager=cacheManager;
        this.idempotencyService=idempotencyService;
    }
    @Retry(name = "redisCache",fallbackMethod = "initiateOrGetExistingFallback")
    @CircuitBreaker(name = "redisCache",fallbackMethod = "initiateOrGetExistingFallback")
    public Optional<IdempotencyKey> initiateOrGetExisting(String key, String requestHash){
        Cache cache=cacheManager.getCache("Idempotency_key");
        IdempotencyKey idempotencyKeyCache=cache.get(key,IdempotencyKey.class);
        if(idempotencyKeyCache!=null){
            if (!idempotencyKeyCache.getRequestHash().equals(requestHash)) {
                throw new RuntimeException("misuse of idempotency key");
            }
            return Optional.of(idempotencyKeyCache);
        }else{
            return idempotencyService.initiateOrGetExistingFromDb(key,requestHash);
        }
    }
    public Optional<IdempotencyKey> initiateOrGetExistingFallback(String key, String requestHash,Exception ex) {
        log.info("Fallback called for initiateOrGetExisting idempotency key");
        log.info("Invocating for Exception "+ex);
        return idempotencyService.initiateOrGetExistingFromDb(key, requestHash);
    }
    }



