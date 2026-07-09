package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("Select oi from OrderItem oi where oi.order.id IN :orderIds")
    List<OrderItem> findAllByOrderIdIn(@Param("orderIds") List<Long> orderIds);
}
