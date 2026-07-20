package com.example.demo.Controller;

import com.example.demo.dto.RevenueReportDto;
import com.example.demo.enums.GroupBy;
import com.example.demo.enums.Period;
import com.example.demo.projection.*;
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

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public RevenueReportDto getRevenue(
            @RequestParam Period period,
            @RequestParam int count,
            @RequestParam(required = false) GroupBy groupBy
    ) {
        return reportService.getRevenueReport(groupBy,period,count);
    }
    @GetMapping("/user_revenue")
    public List<UserRevenueProjection> getRevenueByUser(
            @RequestParam String period,
            @RequestParam int count
    ) {
        return reportService.getUserRevenueReport(period,count);
    }
    @GetMapping("/product_quantity")
    public List<ProductQuantityProjection> getQuantityByProduct(){
        return reportService.getQuantityByProduct();

    }
    @GetMapping("/status_order")
    public List<StatusOrder> getOrderByStatus(){
        return reportService.getOrderByStatus();

    }
    @GetMapping("/stock-health")
    public List<StockHealthProjection> getStockHealth() {
        return reportService.getStockHealth();
    }


}