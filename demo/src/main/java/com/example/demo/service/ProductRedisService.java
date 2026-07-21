package com.example.demo.service;

import com.example.demo.dto.ProductRespsonseRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductRedisService {
    private CacheManager cacheManager;
    private ProductService productService;
    ProductRedisService(CacheManager cacheManager,ProductService productService){
        this.cacheManager=cacheManager;
        this.productService=productService;
    }
    @Retry(name="redisCache",fallbackMethod="getProductByIdFallback")
    @CircuitBreaker(name = "redisCache", fallbackMethod = "getProductByIdFallback")
    @Transactional(readOnly = true)
    public ProductRespsonseRequest getProductById(Long id) {
        log.info("Get product by Id called ");
        Cache cache=cacheManager.getCache("products");
        ProductRespsonseRequest cached=cache.get(id,ProductRespsonseRequest.class);
        if(cached!=null){
            log.info("Cache hit for id "+id);
            return cached;
        }
        log.info("Cache miss for id "+id);
        ProductRespsonseRequest productRespsonseRequest=productService.getProductFromDb(id);
        cache.put(id,productRespsonseRequest);
        return productRespsonseRequest;
    }
    public ProductRespsonseRequest getProductByIdFallback(Long id,Exception ex){
        log.info("Redis failed Failback called etProductByIdFallback for "+id);
        log.info("Exception is "+ex);
        return productService.getProductFromDb(id);
    }


}
