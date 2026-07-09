package com.example.demo.repository;

import com.example.demo.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    @Query("Select i from Invoice i JOIN FETCH i.items Where i.orderId=:id")
    Optional<Invoice> getInvoiceWithItemsByOrderId(@Param("id")Long id);
}
