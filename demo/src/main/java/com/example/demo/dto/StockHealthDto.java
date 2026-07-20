package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHealthDto {
   List<StockHealthItem> stockHealthItemList=new ArrayList<>();
   public void addItem(StockHealthItem stockHealthItem){
       this.stockHealthItemList.add(stockHealthItem);
   }
}
