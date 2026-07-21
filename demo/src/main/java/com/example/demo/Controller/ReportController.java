package com.example.demo.Controller;

import com.example.demo.dto.*;
import com.example.demo.enums.GroupBy;
import com.example.demo.enums.Period;
import com.example.demo.projection.*;
import com.example.demo.service.ReportRedisService;
import com.example.demo.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.GroupPrincipal;
import java.util.List;

@RestController
@RequestMapping(path="/api/reports",produces = "application/json")
public class ReportController {

    private final ReportService reportService;
    private ReportRedisService reportRedisService;

    public ReportController(ReportService reportService,ReportRedisService reportRedisService) {
        this.reportService = reportService;
        this.reportRedisService=reportRedisService;
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public RevenueReportDto getRevenue(
            @RequestParam Period period,
            @RequestParam int count,
            @RequestParam(required = false) GroupBy groupBy
    ) {
        return reportRedisService.getRevenueReport(groupBy,period,count);
    }
    @GetMapping("/user_revenue")
    public UserRevenueDto getRevenueByUser(
            @RequestParam Period period,
            @RequestParam int count
    ) {
        return reportRedisService.getUserRevenueReport(period,count);
    }
    @GetMapping("/product_quantity")
    public ProductQuantityDto getQuantityByProduct(){
        return reportRedisService.getQuantityByProduct();

    }
    @GetMapping("/status_order")
    public StatusOfOrderDto getOrderByStatus(){
        return reportRedisService.getOrderByStatus();

    }
    @GetMapping("/stock-health")
    public StockHealthDto getStockHealth() {
        return reportRedisService.getStockHealth();
    }


}