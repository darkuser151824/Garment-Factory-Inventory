package com.example.demo.service;

import com.example.demo.dto.OrderEntry;
import com.example.demo.dto.OrderEntryRequest;
import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import com.example.demo.enums.Status;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.InsufficientStockException;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private StockRepository stockRepository;
    private OrderItemRepository orderItemRepository;
    public OrderService(OrderRepository orderRepository,ProductRepository productRepository,StockRepository stockRepository,OrderItemRepository orderItemRepository)
    {
        this.orderRepository=orderRepository;
        this.productRepository=productRepository;
        this.stockRepository=stockRepository;
        this.orderItemRepository=orderItemRepository;
    }
    @Transactional
    public void deleteOrder(Long id)
    {
        Order order=orderRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Order with id "+id+" not FOUND for deleting."));
        if (order.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        if (order.getStatus() == Status.DELIVERED) {
            throw new IllegalStateException("Delivered order cannot be cancelled");
        }
        for(OrderItem oi:order.getOrderItemList())
        {
            deleteOrderItem(oi);
        }
        order.setStatus(Status.CANCELLED);
        orderRepository.save(order);
    }
    public void deleteOrderItem(OrderItem orderItem)
    {
        Stock stock=stockRepository.findByPidAndSize(orderItem.getProduct().getPid(),orderItem.getSize());
        stock.setQuantity(stock.getQuantity()+orderItem.getQuantity());
        stockRepository.save(stock);
    }
    public List<OrderResponseRequest> getAllOrders()
    {
        List<Order> list=orderRepository.findAll();
        List<OrderResponseRequest> listorr=new ArrayList<>();
        for(Order order:list)
        {
            listorr.add(mapToEnityOrr(order));
        }
        if(listorr==null)
        {
            throw new ResourceNotFoundException("THere are no orders");
        }
        return listorr;
    }
    @Transactional
    public OrderResponseRequest createOrder(OrderEntryRequest orderEntryRequest)
    {
        Order order=new Order();
        for(OrderEntry oe:orderEntryRequest.getOde())
        {
            OrderItem orderItem=createOrderItem(oe,order);
            order.addItem(orderItem);
        }
        order.setStatus(Status.ORDERED);
        int cost=0;
        int amount=0;
        for(OrderItem oi:order.getOrderItemList())
        {
            cost=cost+oi.getTotalCostOfItem();
            amount=amount+oi.getTotalAmountOfItem();
        }
        order.setTotalAmount(amount);
        order.setTotalCost(cost);
        order.setTotalProfit(amount-cost);
        orderRepository.save(order);

        OrderResponseRequest orderResponseRequest=mapToEnityOrr(order);

        return orderResponseRequest;
    }
    public OrderResponseRequest mapToEnityOrr(Order order)
    {
        OrderResponseRequest orr=new OrderResponseRequest();

        orr.setOid(order.getOrderId());
        orr.setCreatedAt(order.getCreatedAt());
        orr.setStatus(order.getStatus());
        orr.setTotalCost(order.getTotalCost());
        orr.setTotalProfit(order.getTotalProfit());
        orr.setTotalAmount(order.getTotalAmount());
        for(OrderItem oi:order.getOrderItemList())
        {
            orr.getOrderItemResponsesList().add(mapToEntityoir(oi));
        }
        return orr;
    }
    public OrderItemResponse mapToEntityoir(OrderItem orderItem)
    {
        OrderItemResponse orderItemResponse=new OrderItemResponse();
        orderItemResponse.setOiid(orderItem.getOiid());
        orderItemResponse.setPid(orderItem.getProduct().getPid());
        orderItemResponse.setTotalAmountOfItem(orderItem.getTotalAmountOfItem());
        orderItemResponse.setCreatedAt(orderItem.getCreatedAt());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());
        orderItemResponse.setSize(orderItem.getSize());

        return orderItemResponse;

    }


    public OrderItem createOrderItem(OrderEntry orderEntry,Order order)
    {
        Product product=productRepository.findById(orderEntry.getPid())
                .orElseThrow(()->new ResourceNotFoundException(
                        "Product with this pid "+orderEntry.getPid()+" dosent exist"
                ));

        OrderItem orderItem=new OrderItem();
        Size size=orderEntry.getSize();
        updateStock(orderEntry);
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(orderEntry.getQuantity());
        orderItem.setSize(size);
        orderItem=calcCostFields(orderItem,product);

        return orderItem;

    }
    public Stock updateStock(OrderEntry orderEntry)
    {
       Stock stock=stockRepository.findByPidAndSize(orderEntry.getPid(),orderEntry.getSize());
       if(stock.getQuantity()>=orderEntry.getQuantity())
       {
           stock.setQuantity(stock.getQuantity()- orderEntry.getQuantity());
       }else {
           throw new InsufficientStockException("Not Enough Stocks");
       }
       Stock savedStock=stockRepository.save(stock);
       return savedStock;
    }
    public OrderItem calcCostFields(OrderItem orderItem,Product product)
    {
        orderItem.setPriceAtPurchase(product.getSellingPrice());
        orderItem.setTotalAmountOfItem(product.getSellingPrice()* orderItem.getQuantity());
        orderItem.setTotalCostOfItem(product.getCostPerUnit()*orderItem.getQuantity());
        orderItem.setTotalProfitOfItem(orderItem.getTotalAmountOfItem()-orderItem.getTotalCostOfItem());
        return orderItem;

    }


    public OrderResponseRequest getOrderById(Long id) {
        Order order=orderRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("The Order with id "+id+" not FOUND."));

        OrderResponseRequest orderResponseRequest=mapToEnityOrr(order);
        return orderResponseRequest;
    }

    public OrderResponseRequest updateOrderStatus(Long id, Status status) {
        Order order=orderRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("The Order with id "+id+" not FOUND."));
        order.setStatus(status);
        order=orderRepository.save(order);
        return mapToEnityOrr(order);
    }
}
