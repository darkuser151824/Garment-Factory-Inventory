package com.example.demo.Controller;


import com.example.demo.dto.StockEntry;
import com.example.demo.dto.StockResponse;
import com.example.demo.dto.StockUpdateRequest;
import com.example.demo.entity.Stock;
import com.example.demo.exception.ApiResponse;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/stocks",produces="application/json")
public class StockController {

    private StockService stockService;


    public StockController(StockService stockService)
    {
        this.stockService=stockService;
    }
    @GetMapping("/product/{pid}")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStockByProduct(@PathVariable Long pid) {
        List<StockResponse> response=stockService.getStockByProduct(pid);
        return ResponseEntity.ok(new ApiResponse<>(true,"Stock with "+pid+" fetched succesfully",response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStockDetails() {
        List<StockResponse> response=stockService.getAllStock();
        return ResponseEntity.ok(new ApiResponse<>(true,"All Stock Fetched",response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> putStockDetails(@Valid @RequestBody StockEntry stockEntry)
    {
        StockResponse response=stockService.createStock(stockEntry);
        return ResponseEntity.ok(new ApiResponse<>(true,"Stock created sucessfully",response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> updateQuantity(@PathVariable("id") Long id, @Valid @RequestBody StockUpdateRequest updateRequest)
    {
        StockResponse response=stockService.updateStock(id,updateRequest);
      return ResponseEntity.ok(new ApiResponse<>(true,"Stock updated sucessfully",response));
    }

}
