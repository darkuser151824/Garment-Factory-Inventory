package com.example.demo.service;

import com.example.demo.dto.OrderEntry;
import com.example.demo.dto.OrderEntryRequest;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.*;
import com.example.demo.enums.Role;
import com.example.demo.enums.Size;
import com.example.demo.enums.Status;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;
    private Stock testStock;
    private Order testOrder;
    private OrderItem testorderItem;
    private User testuser;

    @BeforeEach
    void setUp() {
        // Create test data used across multiple tests
        testProduct = new Product();
        testProduct.setPid(1L);
        testProduct.setSellingPrice(new BigDecimal("100"));
        testProduct.setCostPerUnit(new BigDecimal("60"));

        testStock = new Stock();
        testStock.setSid(1L);
        testStock.setProduct(testProduct);
        testStock.setSize(Size.MEDIUM);
        testStock.setAvailableQty(50);

        testOrder =new Order();
        testOrder.setOrderId(1L);
        testOrder.setStatus(Status.ORDERED);

        testorderItem =new OrderItem();
        testorderItem.setProduct(testProduct);
        testorderItem.setSize(Size.MEDIUM);
        testorderItem.setOrder(testOrder);
        testorderItem.setQuantity(30);
        testorderItem.setOiid(2L);

        testuser=new User();

        testuser.setUsername("vedansh");
        testuser.setUserId(1L);
        testuser.setRole(Role.ADMIN);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("vedansh", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    @AfterEach
    void cleanup()
    {
        SecurityContextHolder.clearContext();
    }


//    @WithMockUser(username="vedansh",roles="ADMIN")
//    @Test
//    void createOrders_shouldRollback_whenStockinsufficient()
//    {
//        Long orderId=1L;
//        String username="vedansh";
//        OrderEntry orderEntry = new OrderEntry();
//        orderEntry.setPid(1L);
//        orderEntry.setSize(Size.MEDIUM);
//        orderEntry.setQuantity(5);
//
//        OrderEntryRequest request = new OrderEntryRequest();
//        request.setOde(List.of(orderEntry));
//
//        long orderCountBefore=orderRepository.count();
//        int stockCountBefore=teststock.getAvailableQty();
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testuser));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
//        when(stockRepository.findByPidAndSize(1L,Size.MEDIUM)).thenReturn(testStock);
//        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(testOrder);
//
//        assertThrows(ResourceNotFoundException.class,()->{
//            orderService.createOrder(request);
//        });
//        long orderCountAfter = orderRepository.count();
//        int stockQuantityAfter = teststock.getAvailableQty();
//        assertEquals(orderCountBefore,orderCountAfter);
//        assertEquals(stockCountBefore,stockQuantityAfter);
//    }

    @Test
    void deleteOrder_ValidTransition_Success()
    {
        Long orderId=1L;



        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(stockRepository.findByPidAndSize(testorderItem.getProduct().getPid(),testorderItem.getSize())).thenReturn(testStock);
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponseRequest response=orderService.deleteOrder(orderId);

        assertNotNull(response);
        assertEquals(Status.CANCELLED,response.getStatus());
        assertEquals(80 ,testStock.getAvailableQty());

        verify(stockRepository).save(any(Stock.class));
        verify(orderRepository).save(any(Order.class));

    }


    @Test
    void updateOrderStatus_ValidTransition_Success()
    {
        Long orderId=1L;
        Status orderstatus=Status.IN_PRODUCTION;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderResponseRequest response=orderService.updateOrderStatus(orderId,orderstatus);

        assertNotNull(response);
        assertEquals(orderstatus,response.getStatus());

        verify(orderRepository).save(any(Order.class));
        verify(orderRepository).save(argThat(order->order.getStatus()==Status.IN_PRODUCTION));
    }
    @Test
    void updateOrderStatus_IllegalState_ThrowsException()
    {
        testOrder.setStatus(Status.DELIVERED);
        Long orderId=1L;
        Status orderstatus=Status.CANCELLED;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
//        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
//        this was removed due to stubbing acc to this error this method will not be called as before this exception will take place

        assertThrows(IllegalStateException.class,()->{
            orderService.updateOrderStatus(orderId,orderstatus);
        });

        verify(orderRepository,never()).saveAndFlush(any(Order.class));
    }


    @Test
    void createOrder_Success() {
        // Arrange - set up the test scenario
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setPid(1L);
        orderEntry.setSize(Size.MEDIUM);
        orderEntry.setQuantity(5);

        OrderEntryRequest request = new OrderEntryRequest();
        request.setOde(List.of(orderEntry));

        // Mock the repository responses
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(stockRepository.findByPidAndSize(1L, Size.MEDIUM)).thenReturn(testStock);
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L); // Simulate DB assigning ID
            return order;
        });

        // Act - call the method being tested
        OrderResponseRequest response = orderService.createOrder(request);

        // Assert - verify the results
        assertNotNull(response);
        assertEquals(Status.ORDERED, response.getStatus());
        assertEquals(new BigDecimal("500"), response.getTotalAmount()); // 100 * 5
        assertEquals(new BigDecimal("300"), response.getTotalCost());   // 60 * 5
        assertEquals(new BigDecimal("200"), response.getTotalProfit()); // 500 - 300

        // Verify stock was deducted
        verify(stockRepository).save(argThat(stock ->
                stock.getAvailableQty() == 45 // 50 - 5
        ));
    }

    @Test
    void createOrder_InsufficientStock_ThrowsException() {
        // Arrange
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setPid(1L);
        orderEntry.setSize(Size.MEDIUM);
        orderEntry.setQuantity(100); // More than available (50)

        OrderEntryRequest request = new OrderEntryRequest();
        request.setOde(List.of(orderEntry));

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(stockRepository.findByPidAndSize(1L, Size.MEDIUM)).thenReturn(testStock);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(request);
        });

        // Verify order was NOT saved
        verify(orderRepository, never()).saveAndFlush(any(Order.class));
    }
}