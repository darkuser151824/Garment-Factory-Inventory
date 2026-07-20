package com.example.demo.service;

import com.example.demo.Mapper.ReportMapper;
import com.example.demo.dto.RevenueReportDto;
import com.example.demo.dto.RevenueReportItem;
import com.example.demo.enums.GroupBy;
import com.example.demo.enums.Period;
import com.example.demo.projection.*;
import com.example.demo.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

    public List<UserRevenueProjection> getUserRevenueReport(String period, int count) {
        String granularity=reportMapper.mapToGranularity(period);
        LocalDateTime startDate=calculateStartDate(period,count);
        return reportRepository.getRevenueByUser(granularity,startDate);
    }

    public List<ProductQuantityProjection> getQuantityByProduct() {
        return reportRepository.getQuantityByProduct();
    }

    public List<StatusOrder> getOrderByStatus() {
        return reportRepository.getStatusByOrders();
    }

    public List<StockHealthProjection> getStockHealth() {
        return reportRepository.getStockHealth();
    }
}
