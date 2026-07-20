package com.example.demo.Mapper;

import com.example.demo.dto.*;
import com.example.demo.projection.*;
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

    public UserRevenueItem mapToUserRevenueItem(UserRevenueProjection userRevenueProjection) {
        UserRevenueItem userRevenueItem=new UserRevenueItem();
        userRevenueItem.setCost(userRevenueProjection.getCost());
        userRevenueItem.setUser(userRevenueProjection.getUser());
        userRevenueItem.setPeriod(userRevenueProjection.getPeriod());
        userRevenueItem.setProfit(userRevenueProjection.getProfit());
        userRevenueItem.setUsername(userRevenueProjection.getUsername());
        userRevenueItem.setRevenue(userRevenueProjection.getRevenue());
        return userRevenueItem;
    }

    public ProductQuantityItem mapToProductQuantityDto(ProductQuantityProjection productQuantityProjection) {
        ProductQuantityItem productQuantityItem=new ProductQuantityItem();
        productQuantityItem.setColor(productQuantityProjection.getColor());
        productQuantityItem.setPid(productQuantityProjection.getPid());
        productQuantityItem.setFabric(productQuantityProjection.getFabric());
        productQuantityItem.setGarment(productQuantityProjection.getGarment());
        productQuantityItem.setTotalQty(productQuantityProjection.getTotalQty());
        return productQuantityItem;
    }

    public StatusOfOrderItem mapToStatusOrderDto(StatusOrder statusOrder) {
        StatusOfOrderItem statusOfOrderItem=new StatusOfOrderItem();
        statusOfOrderItem.setCount(statusOrder.getCount());
        statusOfOrderItem.setStatus(statusOrder.getStatus());
        return statusOfOrderItem;
    }

    public StockHealthItem mapToStockHealth(StockHealthProjection stockHealthProjection) {
        StockHealthItem stockHealthItem=new StockHealthItem();
        stockHealthItem.setAllocatedQty(stockHealthProjection.getAllocatedQty());
        stockHealthItem.setGarment(stockHealthProjection.getGarment());
        stockHealthItem.setFabric(stockHealthProjection.getFabric());
        stockHealthItem.setColor(stockHealthProjection.getColor());
        stockHealthItem.setSize(stockHealthProjection.getSize());
        stockHealthItem.setAvailableQty(stockHealthProjection.getAvailableQty());
        stockHealthItem.setDispatchedQty(stockHealthProjection.getDispatchedQty());
        stockHealthItem.setReadyQty(stockHealthProjection.getReadyQty());
        stockHealthItem.setProductId(stockHealthProjection.getProductId());
        stockHealthItem.setInProductionQty(stockHealthProjection.getInProductionQty());
        return stockHealthItem;
    }
}
