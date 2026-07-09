package com.example.demo.Mapper;

import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseRequest mapToEnityOrderResponseRequest(Order order, List<OrderItem> orderItemList, Map<Long, Product> productMap)
    {
        OrderResponseRequest orr=new OrderResponseRequest();

        orr.setOid(order.getOrderId());
        orr.setCreatedAt(order.getCreatedAt());
        orr.setStatus(order.getStatus());
        orr.setTotalCost(order.getTotalCost());
        orr.setTotalProfit(order.getTotalProfit());
        orr.setTotalAmount(order.getTotalAmount());
        orr.setCreatedAt(order.getCreatedAt());
        orr.setUpdatedAt(order.getUpdatedAt());
        orr.setUsername(order.getUser().getUsername());
        orr.setUserId(order.getUser().getUserId());
        orr.setOrderItemResponsesList(orderItemList.stream()
                .map(orderItem->mapToEntityOrderItemResponse(orderItem,productMap))
                .collect(Collectors.toList()));
        return orr;
    }

    public OrderItemResponse mapToEntityOrderItemResponse(OrderItem orderItem, Map<Long,Product> productMap)
    {
        OrderItemResponse orderItemResponse=new OrderItemResponse();
        orderItemResponse.setOiid(orderItem.getOiid());
        orderItemResponse.setTotalAmountOfItem(orderItem.getTotalAmountOfItem());
        orderItemResponse.setCreatedAt(orderItem.getCreatedAt());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());
        orderItemResponse.setSize(orderItem.getSize());
        Long productId = orderItem.getProduct().getPid();
        Product product = productMap.get(productId);

        orderItemResponse.setPid(productId);
        orderItemResponse.setPid(orderItem.getProduct().getPid());
        return orderItemResponse;

    }
    public OrderResponseRequest mapToEnityOrderResponseRequest(Order order)
    {
        OrderResponseRequest orr=new OrderResponseRequest();

        orr.setOid(order.getOrderId());
        orr.setCreatedAt(order.getCreatedAt());
        orr.setStatus(order.getStatus());
        orr.setTotalCost(order.getTotalCost());
        orr.setTotalProfit(order.getTotalProfit());
        orr.setTotalAmount(order.getTotalAmount());
        orr.setCreatedAt(order.getCreatedAt());
        orr.setUpdatedAt(order.getUpdatedAt());
        orr.setUsername(order.getUser().getUsername());
        orr.setUserId(order.getUser().getUserId());
        orr.setOrderItemResponsesList(
                order.getOrderItemList() == null ? new ArrayList<>() :
                        order.getOrderItemList()
                                .stream()
                                .map(this::mapToEntityOrderItemResponse)
                                .collect(Collectors.toList())
        );
        return orr;
    }
    public OrderItemResponse mapToEntityOrderItemResponse(OrderItem orderItem)
    {
        OrderItemResponse orderItemResponse=new OrderItemResponse();
        orderItemResponse.setOiid(orderItem.getOiid());
        orderItemResponse.setTotalAmountOfItem(orderItem.getTotalAmountOfItem());
        orderItemResponse.setCreatedAt(orderItem.getCreatedAt());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());
        orderItemResponse.setSize(orderItem.getSize());
        orderItemResponse.setPid(orderItem.getProduct().getPid());
        return orderItemResponse;

    }
}
