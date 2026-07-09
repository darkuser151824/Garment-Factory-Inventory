package com.example.demo.service;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRECONDITION FOR THIS TEST TO RUN AT ALL (not a bug in this file — a datasource setup requirement):
 * The "garment_test" database must actually have invoice_number_seq created.
 * Since test/resources/application.properties currently has spring.flyway.enabled=false
 * and spring.jpa.hibernate.ddl-auto=create-drop, Hibernate only creates tables mapped to @Entity
 * classes — it will NEVER create a raw SEQUENCE, because no entity declares one.
 * Fix required in test/resources/application.properties before this test can pass:
 *     spring.flyway.enabled=true
 *     spring.jpa.hibernate.ddl-auto=validate
 * This lets Flyway create invoice_number_seq (and everything else) exactly like your real DB.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InvoiceServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private InvoiceService invoiceService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;
    private Long testOrderId;
    private String testUsername;

    @Transactional
    @BeforeEach
    void setUp() {
        // Defensive: clear any debris left by a previous crashed run before creating new data.
        // Wrapped so a cleanup failure here (e.g. from an even older run) doesn't block setup.
        try {
            cleanUp();
        } catch (Exception ignored) {
            // If cleanup fails here, the assertions below will surface the real problem clearly
            // instead of this defensive pass masking it.
        }

        // Unique username per run — avoids ANY possibility of unique-constraint collision
        // with leftover data from this test or from other test classes (e.g. StockServiceIntegrationTest).
        testUsername = "invoice_test_" + UUID.randomUUID();

        testUser = new User();
        testUser.setUsername(testUsername);
        testUser.setRole(Role.ADMIN);
        testUser.setPassword("VedaNsh@2737");
        userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setColor(Color.ORANGE);
        testProduct.setFabric(Fabric.POLYESTER);
        testProduct.setGarment(Garment.SHIRT);
        testProduct.setSellingPrice(new BigDecimal("180.00"));
        testProduct.setCostPerUnit(new BigDecimal("350.00"));
        productRepository.save(testProduct);

        testOrder = new Order();
        testOrder.setUser(testUser);
        testOrder.setStatus(Status.DELIVERED);
        testOrder.setTotalAmount(new BigDecimal("360.00"));
        testOrder.setTotalCost(new BigDecimal("700.00"));
        testOrder.setTotalProfit(new BigDecimal("-340.00"));
        orderRepository.save(testOrder);
        testOrderId = testOrder.getOrderId();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(testOrder);
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        orderItem.setSize(Size.MEDIUM);
        orderItem.setPriceAtPurchase(new BigDecimal("180.00"));
        orderItem.setTotalAmountOfItem(new BigDecimal("360.00"));
        orderItem.setTotalCostOfItem(new BigDecimal("700.00"));
        orderItem.setTotalProfitOfItem(new BigDecimal("-340.00"));
        orderItemRepository.save(orderItem);
    }

    @Test
    void twoSimultaneousInvoiceGeneration_shouldOnlyCreateOneInvoice()
            throws ExecutionException, InterruptedException, TimeoutException {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();
        List<InvoiceDTO> results = new CopyOnWriteArrayList<>();

        Runnable task = () -> {
            try {
                latch.await();
                setSecurityContext(testUsername);
                InvoiceDTO invoiceDTO = invoiceService.generateInvoice(testOrderId);
                results.add(invoiceDTO);
                successCount.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                SecurityContextHolder.clearContext();
            }
        };

        Future<?> t1 = executor.submit(task);
        Future<?> t2 = executor.submit(task);

        latch.countDown();
        try {
            t1.get(10, TimeUnit.SECONDS);
            t2.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new RuntimeException("Thread failed unexpectedly", e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("Thread timed out - possible deadlock", e);
        }
        executor.shutdown();

        long invoiceRowCount = invoiceRepository.findAll().stream()
                .filter(i -> i.getOrderId().equals(testOrderId))
                .count();

        // Print any exceptions BEFORE asserting, so a real failure is visible, never swallowed
        for (Exception e : exceptions) {
            System.out.println("Exception caught: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }

        assertTrue(exceptions.isEmpty(),
                "Expected no exceptions - second call should return existing invoice, not throw. See stack traces above if this fails.");
        assertEquals(2, successCount.get(), "Both concurrent calls should return successfully");
        assertEquals(1, invoiceRowCount, "Exactly one invoice row should exist in the DB for this order - locking must prevent duplicates");
        assertEquals(2, results.size(), "Both threads should have returned a result object");
        assertEquals(results.get(0).getInvoiceNumber(), results.get(1).getInvoiceNumber(),
                "Both threads must return the SAME invoice number - no duplicate generated under concurrency");

        System.out.println("Success count: " + successCount.get());
        System.out.println("Invoice row count: " + invoiceRowCount);
        System.out.println("Invoice number returned: " + results.get(0).getInvoiceNumber());
    }

    private void setSecurityContext(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
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
        // Order matters: children before parents, respecting every FK constraint in the schema.
        invoiceRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }
}