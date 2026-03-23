package com.example.demo.dto;

import com.example.demo.entity.Product;
import com.example.demo.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockEntry {

    @NotNull
    @Min(1)
    private Long pid;

    @NotNull
    @Min(1)
    private int quantity;

    @NotNull(message = "Size  cannot be null")
    private Size size;
}
