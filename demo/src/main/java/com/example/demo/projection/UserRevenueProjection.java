package com.example.demo.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UserRevenueProjection {
    LocalDateTime getPeriod();
    String getUser();
    String getUsername();
    BigDecimal getProfit();
    BigDecimal getCost();
    BigDecimal getRevenue();

}
