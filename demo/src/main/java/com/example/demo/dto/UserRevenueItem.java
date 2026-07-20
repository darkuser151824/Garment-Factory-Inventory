package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRevenueItem {
    LocalDateTime period;
    String user;
    String username;
    BigDecimal profit;
    BigDecimal cost;
    BigDecimal revenue;
}
