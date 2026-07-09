package com.example.demo.config;

import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import com.example.demo.enums.Size;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository productRepository,
                               StockRepository stockRepository) {
        return args -> {
            if (productRepository.count() > 0) return;

        };
    }

    private void saveStock(StockRepository repo, Product product, int quantity, Size size) {
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setAvailableQty(quantity);
        stock.setSize(size);
        repo.save(stock);
    }
}