package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {


    @Query("select s from Stock s where s.product.pid=:id and s.size=:size")
    public Stock findByPidAndSize(@Param("id") Long Pid,@Param("size") Size size);

    List<Stock> findByProduct(Product product);
}
