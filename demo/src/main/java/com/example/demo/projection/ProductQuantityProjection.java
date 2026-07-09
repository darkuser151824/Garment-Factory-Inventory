package com.example.demo.projection;

import java.math.BigDecimal;

public interface ProductQuantityProjection {
    String getPid();
    String getGarment();
    String getColor();
    String getFabric();
    BigDecimal getTotalQty();
}
