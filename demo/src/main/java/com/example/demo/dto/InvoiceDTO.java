package com.example.demo.dto;

import com.example.demo.entity.InvoiceItem;
import com.example.demo.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private LocalDateTime generatedAt;
    private String buyerName;
    private String sellerName;
    private String sellerAddress;
    private String sellerGstin;
    private BigDecimal subtotal;
    private BigDecimal gstRate;
    private BigDecimal gstAmount;
    private BigDecimal grandTotal;
    private String paymentStatus;
    private String signatoryName;
    private InvoiceStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InvoiceItemDTO> items = new ArrayList<>();
    public void addItem(InvoiceItemDTO invoiceItemDTO){
        this.items.add(invoiceItemDTO);
    }

}
