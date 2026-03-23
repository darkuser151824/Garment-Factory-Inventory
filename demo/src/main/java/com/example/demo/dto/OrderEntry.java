package com.example.demo.dto;

import com.example.demo.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntry {
    @NotNull(message = "PId is required")
    private Long pid;
    @NotNull(message = "COLOR TYPE IS REQUIRED")
    private Size size;
    @Min(value = 2, message = "Quantity must be greater than 1")
    private int quantity;
}
