package com.example.demo.service;

import com.example.demo.entity.Invoice;
import com.example.demo.entity.InvoiceItem;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvoiceItemService {
    public InvoiceItem generateInvoiceItem(OrderItem orderItem, Invoice invoice){
        InvoiceItem invoiceItem=new InvoiceItem();
        invoiceItem.setQuantity(orderItem.getQuantity());
        invoiceItem.setUnitPrice(orderItem.getPriceAtPurchase());
        invoiceItem.setLineTotal(orderItem.getTotalAmountOfItem());
        Product product = orderItem.getProduct();
        String desc = product.getColor() + " " + product.getFabric() + " " + product.getGarment();
        invoiceItem.setDescription(desc);
        invoiceItem.setInvoice(invoice);
        return  invoiceItem;
    }
}
