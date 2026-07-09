package com.example.demo.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RevenueProjection {
    LocalDateTime getPeriod();
    String getGarment();
    BigDecimal getProfit();
    BigDecimal getCost();
    BigDecimal getRevenue();


}
