package com.example.demo.dto;

import com.example.demo.entity.Stock;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRespsonseRequest {

    private Long pid;


    private Garment garment;


    private Color color;


    private Fabric fabric;


    private int sellingPrice;

    private int costPerUnit;

    private Stock smallStock;
    private Stock mediumStock;
    private Stock largeStock;

}
