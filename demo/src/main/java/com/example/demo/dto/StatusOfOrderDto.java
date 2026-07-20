package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusOfOrderDto {
    List<StatusOfOrderItem> statusOfOrderItemList=new ArrayList<>();
    public void addItem(StatusOfOrderItem statusOfOrderItem){
        this.statusOfOrderItemList.add(statusOfOrderItem);
    }

}
