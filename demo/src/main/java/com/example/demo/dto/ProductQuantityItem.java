package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityItem {
    String pid;
    String garment;
    String color;
    String fabric;
    BigDecimal totalQty;
}
