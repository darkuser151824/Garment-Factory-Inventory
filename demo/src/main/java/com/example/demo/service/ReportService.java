package com.example.demo.service;

import com.example.demo.Mapper.ReportMapper;
import com.example.demo.dto.*;
import com.example.demo.enums.GroupBy;
import com.example.demo.enums.Period;
import com.example.demo.projection.*;
import com.example.demo.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReportService {

    private ReportRepository reportRepository;
    private CacheManager cacheManager;
    private ReportMapper reportMapper;
    ReportService(ReportMapper reportMapper,CacheManager cacheManager,ReportRepository reportRepository){
        this.cacheManager=cacheManager;
        this.reportMapper=reportMapper;
        this.reportRepository=reportRepository;
    }

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
         String granularity=reportMapper.mapToGranularity(period);
         LocalDateTime startDate=calculateStartDate(period,count);
         List<RevenueProjection> revenueProjectionList=reportRepository.getRevenueByGarment(granularity,startDate,groupBy);
        RevenueReportDto revenueReportDto=new RevenueReportDto();
        for(RevenueProjection revenueProjection:revenueProjectionList){
            revenueReportDto.addItem(reportMapper.mapToRevenueReportItem(revenueProjection));
        }
        cache.put(cacheKey,revenueReportDto);
        return revenueReportDto;
    }
    public UserRevenueDto getUserRevenueReport(Period period1, int count) {
        String period=period1.toString();
        String cacheKey=period+":"+count;
        log.info("User Report called for cacheKey "+cacheKey);
        String granularity=reportMapper.mapToGranularity(period);
        LocalDateTime startDate=calculateStartDate(period,count);
        Cache cache=cacheManager.getCache("user_revenue");
        UserRevenueDto cached=cache.get(cacheKey, UserRevenueDto.class);
        if(cached!=null){
            log.info("cache hit for cacheKey "+cacheKey);
            return cached;
        }
        log.info("Cache miss for cache Key"+cacheKey+" Going to postgres");
        List<UserRevenueProjection> userRevenueProjectionList=reportRepository.getRevenueByUser(granularity,startDate);
        UserRevenueDto userRevenueDto=new UserRevenueDto();
        for(UserRevenueProjection userRevenueProjection:userRevenueProjectionList){
            userRevenueDto.addItem(reportMapper.mapToUserRevenueItem(userRevenueProjection));
        }
        cache.put(cacheKey,userRevenueDto);
        return userRevenueDto;
    }


private LocalDateTime calculateStartDate(String period,int count) {
    LocalDateTime now = LocalDateTime.now();
    return switch (period) {
        case "WEEKLY" -> now.minusWeeks(count);
        case "MONTHLY" -> now.minusMonths(count);
        case "QUARTERLY" -> now.minusMonths(count * 3L);
        case "YEARLY" -> now.minusYears(count);
        default -> throw new IllegalArgumentException("Unknown period: " + period);
    };
}



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
        List<ProductQuantityProjection> productQuantityProjectionsList=reportRepository.getQuantityByProduct();
        ProductQuantityDto productQuantityDto=new ProductQuantityDto();
        for(ProductQuantityProjection productQuantityProjection:productQuantityProjectionsList){
            productQuantityDto.addItem(reportMapper.mapToProductQuantityDto(productQuantityProjection));
        }
        cache.put(cacheKey,productQuantityDto);
        return productQuantityDto;
    }

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
        List<StatusOrder> statusOrderList=reportRepository.getStatusByOrders();
        StatusOfOrderDto statusOfOrderDto=new StatusOfOrderDto();
        for(StatusOrder statusOrder:statusOrderList){
            statusOfOrderDto.addItem(reportMapper.mapToStatusOrderDto(statusOrder));
        }
        cache.put(cacheKey,statusOfOrderDto);
        return statusOfOrderDto;
    }

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
        List<StockHealthProjection> stockHealthProjectionsList=reportRepository.getStockHealth();
        StockHealthDto stockHealthDto=new StockHealthDto();
        for(StockHealthProjection stockHealthProjection:stockHealthProjectionsList){
            stockHealthDto.addItem(reportMapper.mapToStockHealth(stockHealthProjection));
        }
        cache.put(cacheKey,stockHealthDto);
        return stockHealthDto;
    }
}
