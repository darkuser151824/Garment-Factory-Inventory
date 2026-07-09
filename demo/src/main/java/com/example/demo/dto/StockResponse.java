package com.example.demo.dto;

import com.example.demo.enums.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    @Min(0)
    private Long pid;
    @Min(0)
    private Long sid;
    private Size size;
    private int quantity;
}
