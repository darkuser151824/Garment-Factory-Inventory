package com.example.demo.service;


import com.example.demo.dto.ProductEntryRequest;
import com.example.demo.dto.ProductRespsonseRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import com.example.demo.enums.Size;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {



    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Stock testSmallStock;
    private Stock testMediumStock;
    private Stock testLargeStock;
    private Product testProduct;
    private ArrayList<Stock> stocklist;

    @BeforeEach
    void setUp()
    {

        testProduct=new Product();
        testProduct.setGarment(Garment.SHIRT);
        testProduct.setFabric(Fabric.POLYESTER);
        testProduct.setColor(Color.BLUE);
        testProduct.setPid(1L);
        testProduct.setSellingPrice(new BigDecimal("850.00"));
        testProduct.setCostPerUnit(new BigDecimal("400.00"));

        testSmallStock=new Stock();
        testSmallStock.setAvailableQty(200);
        testSmallStock.setSize(Size.SMALL);
        testSmallStock.setSid(1L);
        testSmallStock.setProduct(testProduct);

        testMediumStock=new Stock();
        testMediumStock.setAvailableQty(240);
        testMediumStock.setSid(2L);
        testMediumStock.setSize(Size.MEDIUM);
        testMediumStock.setProduct(testProduct);

        testLargeStock=new Stock();
        testLargeStock.setAvailableQty(300);
        testLargeStock.setSize(Size.LARGE);
        testLargeStock.setSid(3L);
        testLargeStock.setProduct(testProduct);

       stocklist=new ArrayList<>(List.of(testSmallStock,testMediumStock,testLargeStock));
    }
//    @Test
//    void deleteProduct_ValidTransition()
//    {
//        Long productId=1L;
//        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
//        when(stockRepository.findByProduct(testProduct)).thenReturn(stocklist);
//        when(stockRepository.delete(any(Stock.class)));
//
//
//    }


    @Test
    void createProduct_ValidTransition()
    {
        ProductEntryRequest productEntryRequest=new ProductEntryRequest();
        productEntryRequest.setColor(Color.BLUE);
        productEntryRequest.setGarment(Garment.SHIRT);
        productEntryRequest.setFabric(Fabric.POLYESTER);
        productEntryRequest.setSellingPrice(new BigDecimal("850.00"));
        productEntryRequest.setCostPerUnit(new BigDecimal("400.00"));
        productEntryRequest.setSmallQty(200);
        productEntryRequest.setMediumQty(240);
        productEntryRequest.setLargeQty(300);

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(stockRepository.save(any(Stock.class))).thenReturn(testSmallStock).thenReturn(testMediumStock).thenReturn(testLargeStock);
        when(stockRepository.findByPidAndSize(1L,Size.SMALL)).thenReturn(testSmallStock);
        when(stockRepository.findByPidAndSize(1L, Size.MEDIUM)).thenReturn(testMediumStock);
        when(stockRepository.findByPidAndSize(1L, Size.LARGE)).thenReturn(testLargeStock);

        ProductRespsonseRequest response=productService.createProduct(productEntryRequest);

        assertNotNull(response);
        assertEquals(Color.BLUE,response.getColor());
        assertEquals(Garment.SHIRT,response.getGarment());
        assertEquals(Fabric.POLYESTER,response.getFabric());
        assertEquals(new BigDecimal("850.00"),response.getSellingPrice());

        assertNotNull(response.getSmallStock());
        assertNotNull(response.getMediumStock());
        assertNotNull(response.getLargeStock());
        assertEquals(200, response.getSmallStock().getQuantity());
        assertEquals(240, response.getMediumStock().getQuantity());
        assertEquals(300, response.getLargeStock().getQuantity());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(stockRepository, times(3)).save(any(Stock.class));
    }

    @Test
    void updateProduct_ValidTransition_Success()
    {
        Long productId=1L;
        ProductEntryRequest productEntryRequest=new ProductEntryRequest();
        productEntryRequest.setColor(Color.BLUE);
        productEntryRequest.setGarment(Garment.SHIRT);
        productEntryRequest.setFabric(Fabric.RAYON);
        productEntryRequest.setSellingPrice(new BigDecimal("1050.00"));
        productEntryRequest.setCostPerUnit(new BigDecimal("430.00"));
        productEntryRequest.setSmallQty(200);
        productEntryRequest.setMediumQty(240);
        productEntryRequest.setLargeQty(300);

        Product updatedProduct = new Product();
        updatedProduct.setPid(1L);
        updatedProduct.setColor(Color.BLUE);
        updatedProduct.setGarment(Garment.SHIRT);
        updatedProduct.setFabric(Fabric.RAYON);
        updatedProduct.setSellingPrice(new BigDecimal("1050.00"));
        updatedProduct.setCostPerUnit(new BigDecimal("430.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(stockRepository.findByPidAndSize(1L,Size.SMALL)).thenReturn(testSmallStock);
        when(stockRepository.findByPidAndSize(1L,Size.MEDIUM)).thenReturn(testMediumStock);
        when(stockRepository.findByPidAndSize(1L,Size.LARGE)).thenReturn(testLargeStock);

        ProductRespsonseRequest response=productService.updateProduct(productId,productEntryRequest);

        assertNotNull(response);

        assertEquals(new BigDecimal("1050.00"),response.getSellingPrice());
        assertEquals(new BigDecimal("430.00"),response.getCostPerUnit());
        assertEquals(Fabric.RAYON,response.getFabric());


        verify(productRepository).save(any(Product.class));
    }
}
