package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDTO{
    private Long id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}