package com.example.demo.dto;

import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import com.example.demo.enums.Size;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Types;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntryRequest {

    @NotNull(message = "Garment type cannot be null")
    private Garment garment;

    @NotNull(message = "Color cannot be null")
    private Color color;

    @NotNull(message = "Fabric type cannot be null")
    private Fabric fabric;

    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be positive")
    @DecimalMin("0.00")
    private BigDecimal sellingPrice;

    @NotNull(message = "Cost per unit is required")
    @Positive(message = "Cost per unit must be positive")
    @DecimalMin("0.00")
    private BigDecimal costPerUnit;

    @NotNull
    @Min(0)
    private Integer smallQty;

    @NotNull
    @Min(0)
    private Integer mediumQty;

    @NotNull
    @Min(0)
    private Integer largeQty;
}


