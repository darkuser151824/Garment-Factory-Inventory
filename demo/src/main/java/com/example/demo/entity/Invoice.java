package com.example.demo.entity;

import com.example.demo.enums.InvoiceStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.entity.InvoiceItem;
import lombok.Data;

@Data
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @Column(name = "seller_name", nullable = false)
    private String sellerName;

    @Column(name = "seller_address", nullable = false)
    private String sellerAddress;

    @Column(name = "seller_gstin", nullable = false)
    private String sellerGstin;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(name = "gst_rate", nullable = false)
    private BigDecimal gstRate;

    @Column(name = "gst_amount", nullable = false)
    private BigDecimal gstAmount;

    @Column(name = "grand_total", nullable = false)
    private BigDecimal grandTotal;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "signatory_name", nullable = false)
    private String signatoryName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();
    public void addItem(InvoiceItem invoiceItem)
    {
        this.items.add(invoiceItem);
    }

    // getters and setters
}