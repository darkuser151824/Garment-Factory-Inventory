package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RevenueReportItem {
    LocalDateTime period;
    String garment;
    BigDecimal profit;
    BigDecimal cost;
    BigDecimal revenue;
}
