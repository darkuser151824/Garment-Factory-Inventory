package com.example.demo.dto;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.enums.Size;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long oiid;

    private Long pid;

    private Date createdAt=new Date();

    private int priceAtPurchase;

    private  int totalAmountOfItem;



    private int quantity;
//    so pid will get us the product and qunatoty the qty the size is done by here only

    private Size size;
}
