package com.example.demo.service;


import com.example.demo.dto.OrderEntry;
import com.example.demo.dto.OrderEntryRequest;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.*;
import com.example.demo.enums.*;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class OrderServiceIntegrationTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    private Stock teststock;
    private Product testProduct;
    private User testUser;
    private User testUser1;
    private OrderItem testorderItem;
    private Order testOrder;
    private OrderEntry orderEntry;
    private OrderEntryRequest orderEntryRequest;
    private Long testStockId;
    private Long testProductId;

    @Transactional
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("vedansh");
        testUser.setPassword("VedaNsh@2737");
        testUser.setRole(Role.ADMIN);
        userRepository.save(testUser);

        testUser1=new User();
        testUser1.setUsername("yash");
        testUser1.setRole(Role.ADMIN);
        testUser1.setPassword("VedaNsh@2737");
        userRepository.save(testUser1);

        testProduct = new Product();
        testProduct.setFabric(Fabric.COTTON);
        testProduct.setGarment(Garment.SHIRT);
        testProduct.setSellingPrice(new BigDecimal("400.00"));
        testProduct.setCostPerUnit(new BigDecimal("200.00"));
        testProduct.setColor(Color.BLUE);
        productRepository.save(testProduct);
        testProductId=testProduct.getPid();

        teststock = new Stock();
        teststock.setAvailableQty(10);
        teststock.setSize(Size.MEDIUM);
        teststock.setProduct(testProduct);
        stockRepository.save(teststock);
        testStockId= teststock.getSid();

        orderEntry=new OrderEntry();
        orderEntry.setPid(testProduct.getPid());
        orderEntry.setQuantity(7);
        orderEntry.setSize(Size.MEDIUM);

        orderEntryRequest=new OrderEntryRequest();
        orderEntryRequest.setOde(List.of(orderEntry));
//
//        OrderResponseRequest orderResponseRequest=orderService.createOrder(orderEntryRequest);
//        testOrder=orderRepository.findById(orderResponseRequest.getOid()).orElseThrow();
    }

    @Transactional
    @AfterEach
    void cleanUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void createOrder_concurrency_pessimistic_happyPath() throws InterruptedException
    {
        ExecutorService executor= Executors.newFixedThreadPool(2);
        CountDownLatch latch=new CountDownLatch(1);
        AtomicInteger successCount=new AtomicInteger(0);
        AtomicInteger failCount=new AtomicInteger(0);

        Future<?> t1=executor.submit(()->{
            try{
                latch.await();
                setSecurityContext("vedansh");
                orderService.createOrder(orderEntryRequest);
                successCount.incrementAndGet();
            }catch (InterruptedException e)
            {
                 Thread.currentThread().interrupt();
            }catch (InsufficientStockException ie) {
                failCount.incrementAndGet();
                System.out.println("Insufficient Stock Exception conlfict on thread :"+Thread.currentThread().getName());
            }finally{
             SecurityContextHolder.clearContext();
            }
        });
        Future<?> t2=executor.submit(()->{
            try{
                latch.await();
                setSecurityContext("yash");
                orderService.createOrder(orderEntryRequest);
                successCount.incrementAndGet();
            }catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }catch (InsufficientStockException ie) {
                failCount.incrementAndGet();
                System.out.println("Insufficient Stock Exception conlfict on thread :"+Thread.currentThread().getName());
            }finally{
                SecurityContextHolder.clearContext();
            }
        });

        latch.countDown();
        try{
            t1.get(10,TimeUnit.SECONDS);
            t2.get(10,TimeUnit.SECONDS);
        }catch(ExecutionException e){
            throw new RuntimeException("Thread failed unexpectedly", e.getCause());
        }catch(TimeoutException te)
        {
            throw new RuntimeException("Thread timed out — possible deadlock", te);
        }

        executor.shutdown();

        Stock finalStock=stockRepository.findByPidAndSize(testProductId,Size.MEDIUM);

        assertEquals(1,successCount.get());
        assertEquals(1,failCount.get());
        assertEquals(3,finalStock.getAvailableQty());

    }
    private void setSecurityContext(String username) {
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                )
        );
        SecurityContextHolder.setContext(context);
    }


    @Test
    @WithMockUser(username = "vedansh",roles="ADMIN")
    void updateOrder_successPath()
    {
        assertEquals(Status.ORDERED, testOrder.getStatus());
        long countBefore = orderRepository.count();

        // Act
        orderService.updateOrderStatus(testOrder.getOrderId(), Status.DELIVERED);

        // Assert
        Order updated = orderRepository.findById(testOrder.getOrderId()).orElseThrow();
        assertEquals(Status.DELIVERED, updated.getStatus());
        assertEquals(countBefore, orderRepository.count()); // no new ro
    }

    @Test
    @WithMockUser(username = "vedansh",roles="ADMIN")
    void createOrder_test_rollback_InsufficientStock()
    {
      //  Arrange
        OrderEntry orderEntry=new OrderEntry();
        orderEntry.setPid(testProduct.getPid());
        orderEntry.setQuantity(30);
        orderEntry.setSize(Size.MEDIUM);

        OrderEntryRequest orderEntryRequest=new OrderEntryRequest();
        orderEntryRequest.setOde(List.of(orderEntry));

        Stock stock1=stockRepository.findByPidAndSize(testProduct.getPid(),teststock.getSize());
        int stockCountBefore=stock1.getAvailableQty();
        long orderCountBefore=orderRepository.count();

//        act
        assertThrows(InsufficientStockException.class,()->{
            OrderResponseRequest orderResponseRequest=orderService.createOrder(orderEntryRequest);
        });

//        assert
        Stock stock2=stockRepository.findByPidAndSize(testProduct.getPid(),teststock.getSize());
        int stockCountAfter=stock2.getAvailableQty();
        long orderCountAfter=orderRepository.count();

        assertEquals(stockCountBefore,stockCountAfter);
        assertEquals(orderCountBefore,orderCountAfter);
    }
    @Test
    @WithMockUser(username = "vedansh", roles = "ADMIN")
    void createOrder_shouldRollback_whenProductNotFound() {

        // Arrange — use a product ID that doesn't exist
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setPid(99999L);  // doesn't exist
        orderEntry.setQuantity(1);
        orderEntry.setSize(Size.MEDIUM);

        OrderEntryRequest request = new OrderEntryRequest();
        request.setOde(List.of(orderEntry));

        long orderCountBefore = orderRepository.count();

        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.createOrder(request)
        );

        // Assert — nothing saved
        assertEquals(orderCountBefore, orderRepository.count());
    }
}
