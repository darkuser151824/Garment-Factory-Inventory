package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHealthItem {
    Long productId;
    String garment;
    String color;
    String fabric;
    String size;
    Integer availableQty;
    Integer allocatedQty;
    Integer inProductionQty;
    Integer readyQty;
    Integer dispatchedQty;
}
