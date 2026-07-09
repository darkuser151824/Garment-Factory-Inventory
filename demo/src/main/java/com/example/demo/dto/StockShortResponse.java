package com.example.demo.dto;

import com.example.demo.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockShortResponse {

    private Long sid;
    private Size size;
    private int quantity;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

