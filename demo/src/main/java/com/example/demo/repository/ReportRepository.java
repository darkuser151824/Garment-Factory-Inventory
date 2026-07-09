package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.projection.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Order,Long> {
    @Query(value= """
            SELECT date_trunc(:granularity,o.created_at) As period,
              CASE 
                WHEN :groupBy='GARMENT' THEN p.garment
                WHEN :groupBy='COLOR' THEN p.color
                WHEN :groupBy='FABRIC' THEN p.fabric
                ELSE NULL
              END AS garment,
              SUM(oi.total_cost_of_item) AS cost,
              SUM(oi.total_amount_of_item) AS revenue,
              SUM(oi.total_profit_of_item) AS profit
             FROM orders o
             JOIN order_item oi ON o.order_id=oi.order_id
             JOIN product p ON oi.p_id=p.pid
             WHERE o.status='DELIVERED' AND o.created_at>=:startDate
             GROUP BY 1,2
             ORDER BY period DESC   
            """,nativeQuery = true)
    List<RevenueProjection> getRevenueByGarment(@Param("granularity") String granularity, @Param("startDate")LocalDateTime startDate, @Param("groupBy") String groupBy);

    @Query(value = """
             SELECT date_trunc(:granularity,o.created_at) AS period,
                o.user_id as user,
                u.username as username,
                SUM(o.total_amount) as revenue,
                SUM(o.total_profit) as profit,
                SUM(o.total_cost) as cost 
                FROM orders o 
                JOIN users u ON o.user_id=u.user_id
                WHERE o.status!='CANCELLED' AND o.created_at>=:startDate
                GROUP BY 3,2,1
                ORDER BY period DESC,SUM(o.total_profit) DESC
            """,nativeQuery = true)
    List<UserRevenueProjection> getRevenueByUser(@Param("granularity")String granularity,@Param("startDate")LocalDateTime startDate);
    @Query(value = """
            SELECT p.pid as Pid,
                   p.garment as garment, 
                   p.color as color, 
                   p.fabric as fabric, 
                   SUM(oi.quantity) as total_qty 
                   FROM order_item oi 
                   JOIN orders o ON oi.order_id = o.order_id 
                   JOIN product p ON oi.p_id = p.pid 
                   WHERE o.status = 'DELIVERED' 
                   GROUP BY p.pid, p.garment, p.color, p.fabric 
                   ORDER BY total_qty DESC 
                   LIMIT 15
            """,nativeQuery = true)
    List<ProductQuantityProjection> getQuantityByProduct();
    @Query(value = """
            SELECT status, COUNT(*) as count FROM orders GROUP BY status;
            """,nativeQuery = true)
    List<StatusOrder> getStatusByOrders();
    @Query(value = """
        SELECT s.product_id AS productId,
               p.garment AS garment,
               p.color AS color,
               p.fabric AS fabric,
               s.size AS size,
               s.available_qty AS availableQty,
               s.allocated_qty AS allocatedQty,
               s.in_production_qty AS inProductionQty,
               s.ready_qty AS readyQty,
               s.dispatched_qty AS dispatchedQty
        FROM stock s
        JOIN product p ON s.product_id = p.pid
        """, nativeQuery = true)
    List<StockHealthProjection> getStockHealth();
}
