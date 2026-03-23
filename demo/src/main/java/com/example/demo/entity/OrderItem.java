package com.example.demo.entity;

import com.example.demo.enums.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long oiid;

    @ManyToOne()
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name="p_id")
    private Product product;

    private Date createdAt=new Date();

    private int priceAtPurchase;

    private  int totalAmountOfItem;

    private int totalProfitOfItem;

    private int totalCostOfItem;

    private int quantity;
//    so pid will get us the product and qunatoty the qty the size is done by here only

    @Enumerated(EnumType.STRING)
    private Size size;



}
