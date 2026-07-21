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
    private ReportMapper reportMapper;
    ReportService(ReportMapper reportMapper,ReportRepository reportRepository){
        this.reportMapper=reportMapper;
        this.reportRepository=reportRepository;
    }

    public RevenueReportDto getRevenueReportByDB(String period, String groupBy, int count) {
        String granularity=reportMapper.mapToGranularity(period);
        LocalDateTime startDate=calculateStartDate(period,count);
        List<RevenueProjection> revenueProjectionList=reportRepository.getRevenueByGarment(granularity,startDate,groupBy);
        RevenueReportDto revenueReportDto=new RevenueReportDto();
        for(RevenueProjection revenueProjection:revenueProjectionList){
            revenueReportDto.addItem(reportMapper.mapToRevenueReportItem(revenueProjection));
        }
        return revenueReportDto;
    }

    public UserRevenueDto getUserRevenueReportFromDB(Period period1, int count){
        String period=period1.toString();
        String granularity=reportMapper.mapToGranularity(period);
        LocalDateTime startDate=calculateStartDate(period,count);
        List<UserRevenueProjection> userRevenueProjectionList=reportRepository.getRevenueByUser(granularity,startDate);
        UserRevenueDto userRevenueDto=new UserRevenueDto();
        for(UserRevenueProjection userRevenueProjection:userRevenueProjectionList){
            userRevenueDto.addItem(reportMapper.mapToUserRevenueItem(userRevenueProjection));
        }
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




    public ProductQuantityDto GetQuantityByProductFromDb(){
        List<ProductQuantityProjection> productQuantityProjectionsList=reportRepository.getQuantityByProduct();
        ProductQuantityDto productQuantityDto=new ProductQuantityDto();
        for(ProductQuantityProjection productQuantityProjection:productQuantityProjectionsList){
            productQuantityDto.addItem(reportMapper.mapToProductQuantityDto(productQuantityProjection));
        }
        return productQuantityDto;
    }


    public StatusOfOrderDto getOrderByStatusFromDb(){
        List<StatusOrder> statusOrderList=reportRepository.getStatusByOrders();
        StatusOfOrderDto statusOfOrderDto=new StatusOfOrderDto();
        for(StatusOrder statusOrder:statusOrderList){
            statusOfOrderDto.addItem(reportMapper.mapToStatusOrderDto(statusOrder));
        }
        return statusOfOrderDto;
    }

    public StockHealthDto getStockHealthFromDb(){
        List<StockHealthProjection> stockHealthProjectionsList=reportRepository.getStockHealth();
        StockHealthDto stockHealthDto=new StockHealthDto();
        for(StockHealthProjection stockHealthProjection:stockHealthProjectionsList){
            stockHealthDto.addItem(reportMapper.mapToStockHealth(stockHealthProjection));
        }
        return stockHealthDto;
    }
}
