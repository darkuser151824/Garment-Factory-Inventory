package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RevenueReportDto {
    List<RevenueReportItem> revenueReportItemList=new ArrayList<>();
    public void addItem(RevenueReportItem revenueReportItem){
        this.revenueReportItemList.add(revenueReportItem);
    }
}
