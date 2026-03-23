package com.example.demo.entity;


import com.example.demo.enums.Size;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","size"}))
public class Stock {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long sid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    private int quantity;

    @Enumerated(EnumType.STRING)
    private Size size;

}
