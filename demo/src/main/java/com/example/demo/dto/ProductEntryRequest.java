package com.example.demo.dto;

import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import com.example.demo.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer sellingPrice;

    @NotNull(message = "Cost per unit is required")
    @Positive(message = "Cost per unit must be positive")
    private Integer costPerUnit;

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


