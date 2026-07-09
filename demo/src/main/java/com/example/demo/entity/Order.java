package com.example.demo.entity;

import com.example.demo.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long orderId;

//    many to one is is eager by default
//    in jpa we just need to make sure it is not chnaged to lazy
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalAmount;

    private BigDecimal totalCost;

    private BigDecimal totalProfit;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE},mappedBy = "order")
    private List<OrderItem> orderItemList=new ArrayList<>();

    @Column(name = "is_invoice_generated", nullable = false)
    private Boolean isInvoiceGenerated = false;

    public void addItem(OrderItem order)
    {
       this.orderItemList.add(order);
    }

}
