package com.example.demo.dto;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.enums.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    @Min(1)
    private Long oiid;

    @Min(1)
    private Long pid;


    private LocalDateTime createdAt;

    @DecimalMin("0.00")
    private BigDecimal priceAtPurchase;

    @DecimalMin("0.00")
    private  BigDecimal totalAmountOfItem;



    private int quantity;
//    so pid will get us the product and qunatoty the qty the size is done by here only

    private Size size;
}
