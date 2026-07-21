package com.example.demo.Controller;


import com.example.demo.dto.OrderEntry;
import com.example.demo.dto.OrderEntryRequest;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.IdempotencyKey;
import com.example.demo.enums.IdempotencyStatus;
import com.example.demo.enums.Status;
import com.example.demo.exception.ApiResponse;
import com.example.demo.service.IdempotencyRedisService;
import com.example.demo.service.IdempotencyService;
import com.example.demo.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping(path="/api/orders",produces = "application/json")
public class OrderController {
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    private IdempotencyService idempotencyService;
    private IdempotencyRedisService idempotencyRedisService;
    public OrderController(IdempotencyRedisService idempotencyRedisService,OrderService orderService,IdempotencyService idempotencyService)
    {
        this.idempotencyRedisService=idempotencyRedisService;
        this.orderService=orderService;
        this.idempotencyService=idempotencyService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Page<OrderResponseRequest>>> getAllOrders(@RequestParam(required = false) Status status,
            @RequestParam(required = false) LocalDateTime createdAt,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) Long userId,
            @PageableDefault(page = 0, size = 10, sort = "orderId") Pageable pageable) {
        Page<OrderResponseRequest> orders = orderService.getAllOrders(status,createdAt,amount,userId,pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders Fetched Successfully", orders));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<OrderResponseRequest>> createOrder(@Valid @RequestBody OrderEntryRequest orderEntryRequest,@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) throws JsonProcessingException {
        if(idempotencyKey == null) {
            return ResponseEntity.status(400).body(new ApiResponse<>(false, "Idempotency-Key header is required", null));
        }
        String json = objectMapper.writeValueAsString(orderEntryRequest);
        String orderEntryHash = DigestUtils.md5DigestAsHex(json.getBytes());
        Optional<IdempotencyKey> idempotencyKey1=idempotencyRedisService.initiateOrGetExisting(idempotencyKey,orderEntryHash);
        if(idempotencyKey1.isEmpty())
        {
            OrderResponseRequest orderResponseRequest=orderService.createOrder(orderEntryRequest);
            String responseBody = objectMapper.writeValueAsString(orderResponseRequest);
            idempotencyService.complete(idempotencyKey,responseBody,201,orderResponseRequest.getOid());
            return ResponseEntity.status(201).body(new ApiResponse<>(true,"Order was created sucesfully",orderResponseRequest));
        }else{
            if(idempotencyKey1.get().getStatus()== IdempotencyStatus.PROCESSING)
            {
                return ResponseEntity.status(409).body(new ApiResponse<>(false, "Request already in progress", null));
            }
            OrderResponseRequest cached = objectMapper.readValue(
                    idempotencyKey1.get().getResponseBody(),
                    OrderResponseRequest.class
            );
            return ResponseEntity.status(idempotencyKey1.get().getHttpStatus()).body(new ApiResponse<>(true,"Order was created sucesfully",cached));
        }

    }

    @DeleteMapping(path="/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable("id") Long oid)
    {
        orderService.deleteOrder(oid);
        return ResponseEntity.status(200).body(new ApiResponse(true,"Order deleted sucessfully","deleted"));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<OrderResponseRequest>> getOrderById(@PathVariable Long id) {
        OrderResponseRequest responseRequest=orderService.getOrderById(id);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Order "+id+" fetched sucesfully",responseRequest));
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<OrderResponseRequest>> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
         OrderResponseRequest orderResponseRequest=orderService.updateOrderStatus(id,status);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Status of "+id+" id changed to "+status,orderResponseRequest));
    }


}
