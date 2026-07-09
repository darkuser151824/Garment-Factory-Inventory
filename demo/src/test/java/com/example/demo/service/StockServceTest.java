package com.example.demo.service;


import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.ModelAttribute;

@ExtendWith(MockitoExtension.class)
public class StockServceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private  StockService stockService;

    @BeforeEach
    void setUp()
    {

    }


}
