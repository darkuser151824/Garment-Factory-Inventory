package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> , JpaSpecificationExecutor<Stock> {


    @Query("select s from Stock s where s.product.pid=:id and s.size=:size")
    public Stock findByPidAndSize(@Param("id") Long Pid,@Param("size") Size size);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name="jakarta.persistence.lock.timeout",value="3000")
    })
    @Query("select s from Stock s where s.product.pid=:id and s.size=:size")
    public Stock findByPidAndSizeAndUpdate(@Param("id") Long Pid,@Param("size") Size size);


    List<Stock> findByProduct(Product product);

    List<Stock> findAllByProductPid(Long pid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name="jakarta.persistence.lock.timeout",value="3000")
    })
    @Query("select s from Stock s where s.product.pid=:id")
    public Stock findByIdAndUpdate(@Param("id")Long pid);

    @Query("select s from Stock s where s.product.pid IN :productIds")
   public List<Stock> findStocksByProductPids(@Param("productIds")List<Long> productIds);

}
