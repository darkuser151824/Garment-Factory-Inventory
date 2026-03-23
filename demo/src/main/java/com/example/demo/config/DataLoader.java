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

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository productRepository,
                               StockRepository stockRepository) {

        return args -> {

            if (productRepository.count() > 0) {
                return; // prevent duplicate insert
            }



            Product p1 = productRepository.save(new Product(
                    null,
                    Garment.SHIRT,
                    Color.BLUE,
                    Fabric.COTTON,
                    500,
                    300
            ));

            Product p2 = productRepository.save(new Product(
                    null,
                    Garment.SHIRT,
                    Color.WHITE,
                    Fabric.RAYON,
                    700,
                    400
            ));

            Product p3 = productRepository.save(new Product(
                    null,
                    Garment.TSHIRT,
                    Color.BLACK,
                    Fabric.COTTON,
                    400,
                    200
            ));

            Product p4 = productRepository.save(new Product(
                    null,
                    Garment.TSHIRT,
                    Color.BLUE,
                    Fabric.POLYESTER,
                    350,
                    180
            ));

            Product p5 = productRepository.save(new Product(
                    null,
                    Garment.SHIRT,
                    Color.BLACK,
                    Fabric.POLYESTER,
                    1200,
                    800
            ));

            // ---------- STOCK ----------

            stockRepository.save(new Stock(null, p1, 50, Size.SMALL));
            stockRepository.save(new Stock(null, p1, 70, Size.MEDIUM));
            stockRepository.save(new Stock(null, p1, 80, Size.LARGE));

            stockRepository.save(new Stock(null, p2, 40, Size.SMALL));
            stockRepository.save(new Stock(null, p2, 60, Size.LARGE));
            stockRepository.save(new Stock(null, p2, 50, Size.MEDIUM));

            stockRepository.save(new Stock(null, p3, 40, Size.SMALL));
            stockRepository.save(new Stock(null, p3, 60, Size.LARGE));
            stockRepository.save(new Stock(null, p3, 50, Size.MEDIUM));
        };
    }
}

