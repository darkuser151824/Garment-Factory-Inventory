package com.example.demo.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "company")
public class CompanyConfig {
    private String name;
    private String address;
    private String gstin;
    private BigDecimal gstRate;       // 0.18
    private String signatoryName;
    private String signatureImagePath;
}