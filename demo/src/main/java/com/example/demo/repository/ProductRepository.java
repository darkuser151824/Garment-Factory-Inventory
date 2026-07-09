package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {

   @Query("select p from Product p where p.fabric=:fabric")
   public List<Product> getProductByFabric(@Param("fabric") Fabric fabric);

    @Query("select p from Product p where p.color=:color")
    public List<Product> getProductByColor(@Param("color") Color color);

    @Query("Select p from Product p WHERE p.pid IN :productIds")
    public List<Product> getProductByIds(@Param("productIds")List<Long> productIds);

    public Page<Product> findByIsDeletedFalse(Pageable pageable);
}
