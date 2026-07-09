package com.example.demo.Mapper;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.dto.InvoiceItemDTO;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceMapper {
    public InvoiceDTO mapToInvoiceResponse(Invoice invoice){
        InvoiceDTO invoiceDTO=new InvoiceDTO();
        invoiceDTO.setId(invoice.getId());
        invoiceDTO.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDTO.setBuyerName(invoice.getBuyerName());
        invoiceDTO.setCreatedAt(invoice.getCreatedAt());
        invoiceDTO.setGeneratedAt(invoice.getGeneratedAt());
        invoiceDTO.setFailureReason(invoice.getFailureReason());
        invoiceDTO.setGrandTotal(invoice.getGrandTotal());
        invoiceDTO.setGstRate(invoice.getGstRate());
        invoiceDTO.setOrderId(invoice.getOrderId());
        invoiceDTO.setGstAmount(invoice.getGstAmount());
        invoiceDTO.setStatus(invoice.getStatus());
        invoiceDTO.setPaymentStatus(invoice.getPaymentStatus());
        invoiceDTO.setSubtotal(invoice.getSubtotal());
        invoiceDTO.setUpdatedAt(invoice.getUpdatedAt());
        invoiceDTO.setSellerAddress(invoice.getSellerAddress());
        invoiceDTO.setSellerName(invoice.getSellerName());
        invoiceDTO.setSellerGstin(invoice.getSellerGstin());
        invoiceDTO.setSignatoryName(invoice.getSignatoryName());

        List<InvoiceItemDTO> itemDTOs = new ArrayList<>();
        for (InvoiceItem item : invoice.getItems()) {
            itemDTOs.add(mapToInvoiceItemDto(item));
        }
        invoiceDTO.setItems(itemDTOs);
        return invoiceDTO;
    }
    public InvoiceItemDTO mapToInvoiceItemDto(InvoiceItem invoiceItem){
        InvoiceItemDTO invoiceItemDTO=new InvoiceItemDTO();
        invoiceItemDTO.setDescription(invoiceItem.getDescription());
        invoiceItemDTO.setQuantity(invoiceItem.getQuantity());
        invoiceItemDTO.setLineTotal(invoiceItem.getLineTotal());
        invoiceItemDTO.setUnitPrice(invoiceItem.getUnitPrice());
        invoiceItemDTO.setId(invoiceItem.getId());
        return invoiceItemDTO;
    }
}
