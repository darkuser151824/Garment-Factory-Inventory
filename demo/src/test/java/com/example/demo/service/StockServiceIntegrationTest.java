package com.example.demo.service;

import com.example.demo.dto.StockUpdateRequest;
import com.example.demo.entity.*;
import com.example.demo.enums.*;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockServiceIntegrationTest {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StockService stockService;

    private Product testProduct;
    private Stock testStock;
    private User testUser;
    private User testUser1;
    private OrderItem orderItem;
    private Order order;
    private StockUpdateRequest testSur1;
    private StockUpdateRequest testSur2;
    private Long stockId;
    private Long testProductId;

    @Transactional
    @BeforeEach
    void setUp()
    {
        testUser=new User();
        testUser.setUsername("vedansh");
        testUser.setRole(Role.ADMIN);
        testUser.setPassword("VedaNsh@2737");
        userRepository.save(testUser);

        testUser1=new User();
        testUser1.setUsername("yash");
        testUser1.setRole(Role.ADMIN);
        testUser1.setPassword("VedaNsh@2737");
        userRepository.save(testUser1);

        testProduct=new Product();
        testProduct.setColor(Color.ORANGE);
        testProduct.setFabric(Fabric.POLYESTER);
        testProduct.setGarment(Garment.SHIRT);
        testProduct.setSellingPrice(new BigDecimal("180.00"));
        testProduct.setCostPerUnit(new BigDecimal("350.00"));
        productRepository.save(testProduct);
        testProductId=testProduct.getPid();

        testStock=new Stock();
        testStock.setProduct(testProduct);
        testStock.setSize(Size.MEDIUM);
        testStock.setAvailableQty(10);
        stockRepository.save(testStock);
        stockId=testStock.getSid();

        testSur1=new StockUpdateRequest();
        testSur1.setNewQuantity(7);

        testSur2=new StockUpdateRequest();
        testSur2.setNewQuantity(7);
    }
    @Test
    void testRaceConditionWithLocking_updateStock() throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService executor= Executors.newFixedThreadPool(2);
        CountDownLatch latch=new CountDownLatch(1);
        AtomicInteger successCount=new AtomicInteger(0);
        AtomicInteger failCount=new AtomicInteger(0);

        Future<?> t1=executor.submit(()->{
            try{
                latch.await();
                setSecurityContext("vedansh");
                stockService.updateStock(stockId,testSur1);
                successCount.incrementAndGet();

            }catch(InterruptedException e){
                 Thread.currentThread().interrupt();
            }catch(InsufficientStockException ie)
            {

                failCount.incrementAndGet();
            }catch(ObjectOptimisticLockingFailureException oe)
            {
                System.out.println("Optimistic lock conflict on thread: " +
                        Thread.currentThread().getName());
                oe.printStackTrace();
                failCount.incrementAndGet();
            } finally {
                SecurityContextHolder.clearContext();
            }
        });

        Future<?> t2=executor.submit(()->{
            try{
                latch.await();
                setSecurityContext("yash");
                stockService.updateStock(stockId,testSur2);
                successCount.incrementAndGet();

            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }catch(InsufficientStockException ie)
            {
                failCount.incrementAndGet();
            }catch(ObjectOptimisticLockingFailureException oe)
            {
                System.out.println("Optimistic lock conflict on thread: " +
                        Thread.currentThread().getName());
                oe.printStackTrace();
                failCount.incrementAndGet();
            } finally {
                SecurityContextHolder.clearContext();
            }
        });

        latch.countDown();
        try {
            t1.get(10, TimeUnit.SECONDS);
            t2.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // unexpected exception escaped the thread
            throw new RuntimeException("Thread failed unexpectedly", e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("Thread timed out — possible deadlock", e);
        }
        executor.shutdown();

        Stock finalStock=stockRepository.findByPidAndSize(testProductId,Size.MEDIUM);

        assertEquals(1,successCount.get(),"Exactly one order should success");
        assertEquals(1,failCount.get(),"The other order will fail");
        assertEquals(3,finalStock.getAvailableQty());

        System.out.println("Success count is "+successCount.get());
        System.out.println("Fail count is "+failCount.get());
        System.out.println("Final stock count is "+finalStock.getAvailableQty());

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

    @AfterEach
    @Transactional
    void cleanUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }
}
