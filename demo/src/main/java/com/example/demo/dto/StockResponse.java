package com.example.demo.dto;

import com.example.demo.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    private Long pid;
    private Long sid;
    private Size size;
    private int quantity;
}
