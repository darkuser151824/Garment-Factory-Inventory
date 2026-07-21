package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.enums.GroupBy;
import com.example.demo.enums.Period;
import com.example.demo.projection.StockHealthProjection;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReportRedisService {
    private CacheManager cacheManager;
    private ReportService reportService;
    ReportRedisService(CacheManager cacheManager,ReportService reportService){
        this.cacheManager=cacheManager;
        this.reportService=reportService;
    }
    @Retry(name="redisCache",fallbackMethod = "getRevenueReportFallback")
    @CircuitBreaker(name="redisCache",fallbackMethod = "getRevenueReportFallback")
    public RevenueReportDto getRevenueReport(GroupBy groupBy1, Period period1, int count){

        String period=period1.toString();
        String groupBy=groupBy1.toString();
        log.info("Revenue Report  called for GroupBy: "+groupBy+" Period:"+period+" count: "+count);
        String cacheKey=period+":"+groupBy+":"+count;
        Cache cache=cacheManager.getCache("revenue");
        RevenueReportDto cached=cache.get(cacheKey,RevenueReportDto.class);
        if(cached!=null){
            log.info("Cache hit for request cache Key "+cacheKey);
            return cached;
        }
        log.info("Cache miss for cache Key"+cacheKey+" Going to postgres");
        RevenueReportDto revenueReportDto=reportService.getRevenueReportByDB(period,groupBy,count);
        cache.put(cacheKey,revenueReportDto);
        return revenueReportDto;
    }
    public RevenueReportDto getRevenueReportFallback(GroupBy groupBy1, Period period1, int count,Exception ex) {
        String period = period1.toString();
        String groupBy = groupBy1.toString();
        log.info("Fallback method getRevenueReportFallback called for groupBy:" + groupBy + " Period:" + period + " count: " + count);
        log.info("Invocated due to exxeption " + ex);
        return reportService.getRevenueReportByDB(period, groupBy, count);
    }

    @Retry(name="redisCache",fallbackMethod = "getUserRevenueReportFallback")
    @CircuitBreaker(name="redisCache",fallbackMethod = "getUserRevenueReportFallback")
    public UserRevenueDto getUserRevenueReport(Period period1, int count) {
        String period=period1.toString();
        String cacheKey=period+":"+count;
        log.info("User Report called for cacheKey "+cacheKey);
        Cache cache=cacheManager.getCache("user_revenue");
        UserRevenueDto cached=cache.get(cacheKey, UserRevenueDto.class);
        if(cached!=null){
            log.info("cache hit for cacheKey "+cacheKey);
            return cached;
        }
        log.info("Cache miss for cache Key"+cacheKey+" Going to postgres");
        UserRevenueDto userRevenueDto=reportService.getUserRevenueReportFromDB(period1,count);
        cache.put(cacheKey,userRevenueDto);
        return userRevenueDto;
    }
    public UserRevenueDto getUserRevenueReportFallback(Period period1,int count,Exception ex){
        log.info("Fallback method called for getUserRevenueReportFallback ");
        log.info("Invocation due to exception "+ex);
        return reportService.getUserRevenueReportFromDB(period1,count);
    }
    @Retry(name="redisCache",fallbackMethod = "getQuantityByProductFallback")
    @CircuitBreaker(name = "redisCache",fallbackMethod = "getQuantityByProductFallback")
    public ProductQuantityDto getQuantityByProduct() {
        log.info("Product quantity report called");
        String cacheKey="product_quantity_key";
        Cache cache=cacheManager.getCache("product_quantity");
        ProductQuantityDto cached=cache.get(cacheKey,ProductQuantityDto.class);
        if(cached!=null){
            log.info("Cache Hit for cacheKey "+cacheKey);
            return cached;
        }
        log.info("Cache miss for cache key "+cacheKey);
        ProductQuantityDto productQuantityDto=reportService.GetQuantityByProductFromDb();
        cache.put(cacheKey,productQuantityDto);
        return productQuantityDto;
    }
    public ProductQuantityDto getQuantityByProductFallback(Exception ex){
        log.info("Fallback called for getQuantityByProduct ");
        log.info("Invocation of fallback due to exceptions "+ex);
        return reportService.GetQuantityByProductFromDb();
    }
    @Retry(name = "redisCache",fallbackMethod = "getOrderByStatusFallback")
    @CircuitBreaker(name = "redisCache",fallbackMethod = "getOrderByStatusFallback")
    public StatusOfOrderDto getOrderByStatus() {
        log.info("Order with status  report called");
        String cacheKey="status_order";
        Cache cache=cacheManager.getCache("status_order");
        StatusOfOrderDto cached=cache.get(cacheKey,StatusOfOrderDto.class);
        if(cached!=null){
            log.info("Cache Hit for cacheKey "+cacheKey);
            return cached;
        }
        log.info("Cache miss for cache key "+cacheKey);
        StatusOfOrderDto statusOfOrderDto=reportService.getOrderByStatusFromDb();
        cache.put(cacheKey,statusOfOrderDto);
        return statusOfOrderDto;
    }
    public StatusOfOrderDto getOrderByStatusFallback(Exception ex){
        log.info("fallback called for getOrderBystatus ");
        log.info("Invocation due to exception "+ex);
        return reportService.getOrderByStatusFromDb();
    }

    @Retry(name = "redisCache",fallbackMethod = "StockHealthDtoFallback")
    @CircuitBreaker(name = "redisCache",fallbackMethod = "StockHealthDtoFallback")
    public StockHealthDto getStockHealth() {
        log.info("Stock health Projection called");
        String cacheKey="stock_health";
        Cache cache=cacheManager.getCache("stock_health");
        StockHealthDto cached=cache.get(cacheKey,StockHealthDto.class);
        if(cached!=null){
            log.info("cache hit for cacheKey "+cacheKey);
            return cached;
        }
        log.info("Cache miss for cache key "+cacheKey);
        StockHealthDto stockHealthDto=reportService.getStockHealthFromDb();
        cache.put(cacheKey,stockHealthDto);
        return stockHealthDto;
    }
    public StockHealthDto StockHealthDtoFallback(Exception ex){
        log.info("Fallback called for StockHealthDto");
        log.info("Invocated for exception "+ex);
        return reportService.getStockHealthFromDb();
    }



}
