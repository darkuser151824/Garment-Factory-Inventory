package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityDto {
    List<ProductQuantityItem> productQuantityItemList=new ArrayList<>();
    public void addItem(ProductQuantityItem productQuantityItem){
        this.productQuantityItemList.add(productQuantityItem);
    }

}
