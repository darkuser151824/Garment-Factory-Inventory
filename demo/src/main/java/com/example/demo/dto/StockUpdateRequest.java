package com.example.demo.dto;

import com.example.demo.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StockUpdateRequest {
    private int newQuantity;
}
