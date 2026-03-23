package com.example.demo.dto;

import com.example.demo.entity.OrderItem;
import com.example.demo.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseRequest {
    private Long oid;

    private Status status;

    private Date createdAt;

    private int totalAmount;

    private int totalCost;

    private int totalProfit;

    private List<OrderItemResponse> orderItemResponsesList=new ArrayList<>();

    public void addItem(OrderItemResponse orderItemResponse)
    {
        this.orderItemResponsesList.add(orderItemResponse);
    }
}
