package com.example.demo.service;

import com.example.demo.Mapper.OrderMapper;
import com.example.demo.dto.OrderEntry;
import com.example.demo.dto.OrderEntryRequest;
import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.*;
import com.example.demo.enums.Size;
import com.example.demo.enums.Status;
import com.example.demo.event.OrderDeliveredEvent;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.specification.OrderSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private StockRepository stockRepository;
    private UserRepository userRepository;
    private OrderItemService orderItemService;
    private OrderItemRepository orderItemRepository;
    private OrderMapper orderMapper;
    private OrderTransitionService orderTransitionService;
    private StockTransitionService stockTransitionService;
    private ApplicationEventPublisher eventPublisher;
    private InvoiceService invoiceService;



    public OrderService(InvoiceService invoiceService,ApplicationEventPublisher eventPublisher,OrderTransitionService orderTransitionService,StockTransitionService stockTransitionService,OrderMapper orderMapper,OrderItemService orderItemService,OrderRepository orderRepository,ProductRepository productRepository,StockRepository stockRepository,OrderItemRepository orderItemRepository,UserRepository userRepository)
    {
        this.invoiceService=invoiceService;
        this.orderTransitionService=orderTransitionService;
        this.stockTransitionService=stockTransitionService;
        this.orderMapper=orderMapper;
        this.orderItemRepository=orderItemRepository;
        this.orderRepository=orderRepository;
        this.productRepository=productRepository;
        this.stockRepository=stockRepository;
        this.userRepository=userRepository;
        this.orderItemService=orderItemService;
        this.eventPublisher=eventPublisher;
    }


    @Transactional(readOnly = true)
    public Page<OrderResponseRequest> getAllOrders(Status status, LocalDateTime createdAt,BigDecimal amount,Long userId,Pageable pageable) {
        log.info("getAllOrders called  for status {}, created after {} and more than amount {}",status,createdAt,amount);
        Specification<Order> spec= OrderSpecification.hasStatus(status)
                .and(OrderSpecification.hasCreatedAt(createdAt))
                .and(OrderSpecification.hasAmount(amount));
        Page<Order> orderPage=orderRepository.findAll(spec,pageable);
        List<Order> orderList=orderPage.getContent();
        List<Long> orderIds=orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
        List<OrderItem > orderItemList=orderItemRepository.findAllByOrderIdIn(orderIds);
        Map<Long,List<OrderItem>> orderItemMap=orderItemList.stream()
                .collect(Collectors.groupingBy(oi->oi.getOrder().getOrderId()));

//      // mapping the second level depth the produc t
        List<Long> productId=orderItemList.stream()
                .map(oi->oi.getProduct().getPid())
                .distinct()
                .collect(Collectors.toList());
        List<Product> productList=productRepository.getProductByIds(productId);

        Map<Long,Product> productMap=productList.stream()
                .collect(Collectors.toMap(p->p.getPid(),p->p));
        log.debug("Returning getallorders with {} orders ,{} orderItems",orderList.size(),orderItemList.size());

        return orderPage.map(order ->
            orderMapper.mapToEnityOrderResponseRequest(order,orderItemMap.getOrDefault(order.getOrderId(),List.of()),productMap)
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            timeout = 30)
    public OrderResponseRequest createOrder(OrderEntryRequest orderEntryRequest)
    {
        log.info("Create order called with {} items",orderEntryRequest.getOde().size());
        List<OrderEntry> sortedOrderEntry = orderEntryRequest.getOde()
                .stream()
                .sorted(Comparator.comparing(OrderEntry::getPid))
                .collect(Collectors.toList());
        List<Long> productIds=sortedOrderEntry.stream()
                .map(orderEntry -> orderEntry.getPid())
                .distinct()
                .collect(Collectors.toList());
        List<Product> productList=productRepository.getProductByIds(productIds);

        Map<Long,Product> productMap=productList.stream()
                .collect(Collectors.toMap(p->p.getPid(),p->p));



        Order order=new Order();
        order.setStatus(Status.ORDERED);
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
//        1

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        order.setUser(user);
        log.info("The user  {} attached to order  ",username);
        List<OrderEntry> sorted = orderEntryRequest.getOde()
                .stream()
                .sorted(Comparator.comparing(OrderEntry::getPid))
                .collect(Collectors.toList());
        for(OrderEntry oe:sorted)
        {
            OrderItem orderItem=orderItemService.createOrderItem(oe,order,productMap.getOrDefault(oe.getPid(),null));
            order.addItem(orderItem);
        }
        BigDecimal cost=BigDecimal.ZERO;
        BigDecimal amount=BigDecimal.ZERO;
        for(OrderItem oi:order.getOrderItemList())
        {
            cost=cost.add(oi.getTotalCostOfItem());
            amount=amount.add(oi.getTotalAmountOfItem());
        }
        order.setTotalAmount(amount);
        order.setTotalCost(cost);
        order.setTotalProfit(amount.subtract(cost));

        orderRepository.saveAndFlush(order);

        log.info("Order created successfully, orderId={}, totalAmount={}",
                order.getOrderId(), order.getTotalAmount());

        OrderResponseRequest orderResponseRequest= orderMapper.mapToEnityOrderResponseRequest(order);

        return orderResponseRequest;
    }

    @Transactional
    public OrderResponseRequest deleteOrder(Long id)
    {
        log.info("Delete order called for {} order ",id);
        Order order=orderRepository.findByIdWithItems(id).orElseThrow(
                ()->new ResourceNotFoundException("Order with id "+id+" not FOUND for deleting."));
        if (order.getStatus() == Status.CANCELLED) {
            log.warn("Order {} is already cancelled ",id);
            throw new IllegalStateException("Order already cancelled");
        }

        if (order.getStatus() == Status.DELIVERED) {
            log.warn("Order {} is already delivered ",id);
            throw new IllegalStateException("Delivered order cannot be cancelled");
        }
//        List<OrderItem> orderItemList=orderItemRepository.findAllByOrderIdIn(List.of(id));
        for(OrderItem oi:order.getOrderItemList())
        {
            orderItemService.deleteOrderItem(oi);
        }
        order.setStatus(Status.CANCELLED);
        order.setDeleted(true);
        Order savedOrder=orderRepository.save(order);

        OrderResponseRequest orderResponseRequest= orderMapper.mapToEnityOrderResponseRequest(savedOrder);
        log.info("Order {} and its {} Orderitem deleted succesfully",order.getOrderId(),order.getOrderItemList().size());

        return orderResponseRequest;
    }




    @Transactional
    public OrderResponseRequest updateOrderStatus(Long id, Status status) {
        log.info("updateOrderStatus is called for order {} to status {}",id,status);
        Order order=orderRepository.findByIdWithItems(id).orElseThrow(
                ()->new ResourceNotFoundException("The Order with id "+id+" not FOUND."));
//  findByIdWithItems loads all the iems eager like all orderitems also but we only need order so
//  maybe can chnage to findById and fetch just the order and use lazy laoding to impove performace
        Status previousStatus=order.getStatus();
        orderTransitionService.validate(previousStatus,status);

        order.setStatus(status);
        order=orderRepository.save(order);

        // Fire stock transition based on new status
        List<OrderItem> items = order.getOrderItemList();
        switch (status) {
            case IN_PRODUCTION -> stockTransitionService.onInProduction(items);
            case READY         -> stockTransitionService.onOrderReady(items);
            case DELIVERED     -> stockTransitionService.onOrderDelivered(items);
            case CANCELLED     -> stockTransitionService.onOrderCancelled(items, previousStatus);
        }

        log.info("Order {} status changed to {} successfully, stock transition fired", id, status);
        if (status == Status.DELIVERED) {
            eventPublisher.publishEvent(new OrderDeliveredEvent(id));
        }


        return orderMapper.mapToEnityOrderResponseRequest(order);

    }

    @Transactional(readOnly = true)
    public OrderResponseRequest getOrderById(Long id) {
        log.info("GetorderById called for Order {}",id);
        Order order=orderRepository.findByIdWithItems(id).orElseThrow(
                ()->new ResourceNotFoundException("The Order with id "+id+" not FOUND."));

        OrderResponseRequest orderResponseRequest= orderMapper.mapToEnityOrderResponseRequest(order);
        log.info("Order {} succesfully fetched ",id);
        return orderResponseRequest;
    }
}
