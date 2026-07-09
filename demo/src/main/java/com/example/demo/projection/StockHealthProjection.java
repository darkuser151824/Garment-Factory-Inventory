package com.example.demo.projection;

public interface StockHealthProjection {
    Long getProductId();
    String getGarment();
    String getColor();
    String getFabric();
    String getSize();
    Integer getAvailableQty();
    Integer getAllocatedQty();
    Integer getInProductionQty();
    Integer getReadyQty();
    Integer getDispatchedQty();
}