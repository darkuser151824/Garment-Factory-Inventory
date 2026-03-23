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

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( uniqueConstraints = @UniqueConstraint(columnNames = {"garment","color","fabric"}))
public class Product {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
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



   private int sellingPrice;

   private int costPerUnit;
}
