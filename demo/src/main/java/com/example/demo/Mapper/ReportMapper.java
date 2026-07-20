package com.example.demo.Mapper;

import com.example.demo.dto.RevenueReportItem;
import com.example.demo.projection.RevenueProjection;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {
    public RevenueReportItem mapToRevenueReportItem(RevenueProjection revenueProjection){
        RevenueReportItem revenueReportItem=new RevenueReportItem();
        revenueReportItem.setRevenue(revenueProjection.getRevenue());
        revenueReportItem.setGarment(revenueProjection.getGarment());
        revenueReportItem.setCost(revenueProjection.getCost());
        revenueReportItem.setProfit(revenueProjection.getProfit());
        revenueReportItem.setPeriod(revenueProjection.getPeriod());
        return revenueReportItem;
    }
    public String mapToGranularity(String period){
        return switch (period){
            case "WEEKLY" -> "week";
            case "MONTHLY"->"month";
            case "QUARTERLY"->"quarter";
            case "YEARLY"->"year";
            default -> throw new IllegalArgumentException("Unknown period: " + period);
        };
    }
}
