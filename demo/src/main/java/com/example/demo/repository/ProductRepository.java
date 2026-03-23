package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

   @Query("select p from Product p where p.fabric=:fabric")
   public List<Product> getProductByFabric(@Param("fabric") Fabric fabric);

    @Query("select p from Product p where p.color=:color")
    public List<Product> getProductByColor(@Param("color") Color color);
}
