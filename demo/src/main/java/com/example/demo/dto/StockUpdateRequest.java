package com.example.demo.dto;

import com.example.demo.enums.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StockUpdateRequest {

    @Min(1)
    private int newQuantity;
}
