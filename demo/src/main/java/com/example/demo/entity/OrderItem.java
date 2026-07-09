package com.example.demo.entity;

import com.example.demo.enums.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oiid;

    @ManyToOne()
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="p_id")
    private Product product;

    private BigDecimal priceAtPurchase;

    private  BigDecimal totalAmountOfItem;

    private BigDecimal totalProfitOfItem;

    private BigDecimal totalCostOfItem;

    private int quantity;
//    so pid will get us the product and qunatoty the qty the size is done by here only

    @Enumerated(EnumType.STRING)
    private Size size;

}
