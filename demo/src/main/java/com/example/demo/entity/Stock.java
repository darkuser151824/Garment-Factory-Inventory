package com.example.demo.entity;


import com.example.demo.enums.Size;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","size"}))
@EntityListeners(AuditingEntityListener.class)
public class Stock extends BaseEntity{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long sid;


    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    private Integer availableQty;
    private Integer allocatedQty;
    private Integer inProductionQty;
    private Integer readyQty;
    private Integer dispatchedQty;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Version
    private Long version;

}
