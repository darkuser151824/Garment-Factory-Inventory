package com.example.demo.entity;

import com.example.demo.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long orderId;

    private Date createdAt=new Date();

    @Enumerated(EnumType.STRING)
    private Status status;

    private int totalAmount;

    private int totalCost;

    private int totalProfit;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "order")
    private List<OrderItem> orderItemList=new ArrayList<>();

    public void addItem(OrderItem order)
    {
       this.orderItemList.add(order);
    }

}
