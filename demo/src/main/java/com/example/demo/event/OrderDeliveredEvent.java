package com.example.demo.event;

public class OrderDeliveredEvent {
    private final long orderId;
    public OrderDeliveredEvent(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

}
