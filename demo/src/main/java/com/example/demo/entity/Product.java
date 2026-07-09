package com.example.demo.entity;

import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = @UniqueConstraint(columnNames = {"garment","color","fabric"}))
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseEntity{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long pid;

   @NotNull(message = "GARMENT TYPE IS REQUIRED")
   @Enumerated(EnumType.STRING)
   private Garment garment;

   @NotNull(message = "COLOR TYPE IS REQUIRED")
   @Enumerated(EnumType.STRING)
   private Color color;

   @NotNull
   @Enumerated(EnumType.STRING)
   private Fabric fabric;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Stock> stockList;

   private BigDecimal sellingPrice;

   private BigDecimal costPerUnit;
}
