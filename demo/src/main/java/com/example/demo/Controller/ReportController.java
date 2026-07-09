package com.example.demo.controller;

import com.example.demo.projection.*;
import com.example.demo.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/reports/revenue")
    public List<RevenueProjection> getRevenue(
            @RequestParam String period,
            @RequestParam int count,
            @RequestParam(required = false) String groupBy
    ) {
        return reportService.getRevenueReport(groupBy,period,count);
    }
    @GetMapping("/api/reports/user_revenue")
    public List<UserRevenueProjection> getRevenueByUser(
            @RequestParam String period,
            @RequestParam int count
    ) {
        return reportService.getUserRevenueReport(period,count);
    }
    @GetMapping("/api/reports/product_quantity")
    public List<ProductQuantityProjection> getQuantityByProduct(){
        return reportService.getQuantityByProduct();

    }
    @GetMapping("/api/reports/status_order")
    public List<StatusOrder> getOrderByStatus(){
        return reportService.getOrderByStatus();

    }
    @GetMapping("/api/reports/stock-health")
    public List<StockHealthProjection> getStockHealth() {
        return reportService.getStockHealth();
    }


}