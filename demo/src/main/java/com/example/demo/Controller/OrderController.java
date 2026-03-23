package com.example.demo.Controller;


import com.example.demo.dto.OrderEntry;
import com.example.demo.dto.OrderEntryRequest;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.enums.Status;
import com.example.demo.exception.ApiResponse;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(path="/api/orders",produces = "application/json")
public class OrderController {
    private OrderService orderService;
    public OrderController(OrderService orderService)
    {
        this.orderService=orderService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseRequest>>> getAllOrders()
    {
        List<OrderResponseRequest> listoforders=orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse<>(true,"Orders Fectched Sucesfully",listoforders));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseRequest>> postOrder(@Valid @RequestBody OrderEntryRequest orderEntryRequest)
    {
        OrderResponseRequest orr=orderService.createOrder(orderEntryRequest);
        return ResponseEntity.ok(new ApiResponse<>(true,"Order was created sucesfully",orr));
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable("id") Long oid)
    {
        orderService.deleteOrder(oid);
        return ResponseEntity.ok(new ApiResponse(true,"Order deleted sucessfully","deleted"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseRequest>> getOrderById(@PathVariable Long id) {
        OrderResponseRequest responseRequest=orderService.getOrderById(id);
        return ResponseEntity.ok(new ApiResponse<>(true,"Order "+id+" fetched sucesfully",responseRequest));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponseRequest>> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
         OrderResponseRequest orderResponseRequest=orderService.updateOrderStatus(id,status);
        return ResponseEntity.ok(new ApiResponse<>(true,"Status of "+id+" id changed to "+status,orderResponseRequest));
    }


}
