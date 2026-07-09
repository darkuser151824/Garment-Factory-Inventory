package com.example.demo.service;

import com.example.demo.projection.*;
import com.example.demo.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private ReportRepository reportRepository;
    ReportService(ReportRepository reportRepository){
        this.reportRepository=reportRepository;
    }

    public List<RevenueProjection> getRevenueReport(String groupBy,String period,int count){
         String granularity=mapToGranularity(period);
         LocalDateTime startDate=calculateStartDate(period,count);
         return reportRepository.getRevenueByGarment(granularity,startDate,groupBy);
    }
    private String mapToGranularity(String period){
        return switch (period){
            case "WEEKLY" -> "week";
            case "MONTHLY"->"month";
            case "QUARTERLY"->"quarter";
            case "YEARLY"->"year";
            default -> throw new IllegalArgumentException("Unknown period: " + period);
        };
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
        String granularity=mapToGranularity(period);
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
