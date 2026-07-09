package com.example.demo.listener;

import com.example.demo.event.OrderDeliveredEvent;
import com.example.demo.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderDeliveredListener {

    private final InvoiceService invoiceService;

    public OrderDeliveredListener(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Async
    @EventListener
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        log.info("EVENT FIRED - OrderDeliveredEvent received for orderId {}", event.getOrderId());
        invoiceService.generateInvoice(event.getOrderId());
    }
}