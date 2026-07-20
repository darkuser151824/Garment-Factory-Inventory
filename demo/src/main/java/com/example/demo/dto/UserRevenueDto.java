package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRevenueDto {
    List<UserRevenueItem> userRevenueItemList=new ArrayList<>();
    public void addItem(UserRevenueItem userRevenueItem){
        this.userRevenueItemList.add(userRevenueItem);
    }
}
