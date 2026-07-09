package com.example.demo.service;

import com.example.demo.dto.OrderEntry;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class OrderItemService {
    private ProductRepository productRepository;
    private StockService stockService;
    private StockRepository stockRepository;

    OrderItemService(StockRepository stockRepository,ProductRepository productRepository,StockService stockService)
    {
        this.productRepository=productRepository;
        this.stockService=stockService;
        this.stockRepository=stockRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItem createOrderItem(OrderEntry orderEntry, Order order,Product product)
    {
        log.info("create Order Item called for {} products of {}",orderEntry.getSize(),orderEntry.getPid());
       if(product==null)
       {
           throw new ResourceNotFoundException("Product with this pid "+orderEntry.getPid()+" dosent exist");
       }

        OrderItem orderItem=new OrderItem();
        Size size=orderEntry.getSize();
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(orderEntry.getQuantity());
        orderItem.setSize(size);
        orderItem=calcCostFields(orderItem,product);
        stockService.updateStock(orderEntry);
        log.info("Order item {} created sucessfully ",orderItem.getOiid());

        return orderItem;
    }
    public OrderItem calcCostFields(OrderItem orderItem,Product product)
    {
        orderItem.setPriceAtPurchase(product.getSellingPrice());
        orderItem.setTotalAmountOfItem(product.getSellingPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        orderItem.setTotalCostOfItem(product.getCostPerUnit().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        orderItem.setTotalProfitOfItem(orderItem.getTotalAmountOfItem().subtract(orderItem.getTotalCostOfItem()));
        return orderItem;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOrderItem(OrderItem orderItem)
    {
        log.info("Orderitem {} called for delete",orderItem.getOiid());
        Stock stock=stockRepository.findByPidAndSizeAndUpdate(orderItem.getProduct().getPid(),orderItem.getSize());
        if (stock == null) {
            log.warn("Stock not found for product {}, size {} for orderItem",orderItem.getProduct().getPid(),orderItem.getSize(),orderItem.getOiid());
            throw new ResourceNotFoundException(
                    "Stock not found for product " + orderItem.getProduct().getPid()
                            + " size " + orderItem.getSize()
            );
        }
        stock.setAvailableQty(stock.getAvailableQty()+orderItem.getQuantity());
        log.info("Stock quantity restored to {} from {]",stock.getAvailableQty()+orderItem.getQuantity(),stock.getAvailableQty());
        stockRepository.save(stock);
        orderItem.setDeleted(true);
    }


}
