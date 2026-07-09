package com.example.demo.dto;

import com.example.demo.entity.Stock;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRespsonseRequest {

    private Long pid;


    private Garment garment;


    private Color color;


    private Fabric fabric;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



    private BigDecimal sellingPrice;


    private BigDecimal costPerUnit;


    private StockShortResponse smallStock;

    private StockShortResponse mediumStock;

    private StockShortResponse largeStock;

}
