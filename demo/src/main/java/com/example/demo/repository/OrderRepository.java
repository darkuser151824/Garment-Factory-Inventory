package com.example.demo.repository;

import com.example.demo.entity.Order;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.aspectj.weaver.ast.Or;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> , JpaSpecificationExecutor<Order> {

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.orderItemList WHERE o.isDeleted = false",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.isDeleted = false")
    Page<Order> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItemList oi LEFT JOIN FETCH oi.product WHERE o.orderId = :id AND o.isDeleted = false")
    Optional<Order> findByIdWithItems(@Param("id") Long id);


    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItemList oi LEFT JOIN FETCH o.user LEFT JOIN FETCH oi.product WHERE o.orderId = :id AND o.isDeleted = false")
    Optional<Order> findByIdWithItemsAndUser(@Param("id") Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name="jakarta.persistence.lock.timeout",value="3000")
    })
    @Query("SELECT o FROM Order o WHERE o.orderId=:id")
    Optional<Order> findWithIdAndLock(@Param("id")Long id);
}
