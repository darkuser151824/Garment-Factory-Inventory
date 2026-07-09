package com.example.demo.dto;

import com.example.demo.entity.OrderItem;
import com.example.demo.enums.Status;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseRequest {
    private Long oid;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @DecimalMin("0.00")
    private BigDecimal totalAmount;

    @DecimalMin("0.00")
    private BigDecimal totalCost;

    @DecimalMin("0.00")
    private BigDecimal totalProfit;

    private List<OrderItemResponse> orderItemResponsesList=new ArrayList<>();
    private String username;
    private Long userId;


    public void addItem(OrderItemResponse orderItemResponse)
    {
        this.orderItemResponsesList.add(orderItemResponse);
    }
}
