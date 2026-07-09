package com.example.demo.specification;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.enums.Color;
import com.example.demo.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> hasStatus(Status status)
    {
        return (root,query,cb)->
            status == null ? null : cb.equal(root.get("status"),status);
    }

    public static Specification<Order> hasCreatedAt(LocalDateTime createdAt)
    {
        return (root,query,cb)->
                createdAt == null ? null : cb.greaterThan(root.get("createdAt"),createdAt);
    }
    public static Specification<Order> hasAmount(BigDecimal amount)
    {
        return (root,query,cb)->
                amount == null ? null : cb.greaterThan(root.get("totalAmount"),amount);
    }
    public static Specification<Order> hasUserId(Long userId)
    {
        return (root,query,cb)->
                userId==null?null:cb.equal(root.get("user_id"),userId);
    }




}
