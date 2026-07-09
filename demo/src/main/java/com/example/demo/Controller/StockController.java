package com.example.demo.Controller;


import com.example.demo.dto.StockEntry;
import com.example.demo.dto.StockResponse;
import com.example.demo.dto.StockUpdateRequest;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import com.example.demo.exception.ApiResponse;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.StockRetryService;
import com.example.demo.service.StockService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/stocks",produces="application/json")
public class StockController {

    private StockService stockService;
    private StockRetryService stockRetryService;


    public StockController(StockService stockService,StockRetryService stockRetryService)
    {
        this.stockService=stockService;
        this.stockRetryService=stockRetryService;
    }
    @GetMapping("/product/{pid}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStockByProduct(@PathVariable Long pid) {
        List<StockResponse> response=stockService.getStockByProduct(pid);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Stock with "+pid+" fetched succesfully",response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Page<StockResponse>>> getStockDetails(@RequestParam(required = false) Size stockSize,
                                                                            @RequestParam(required = false) Long pid,
                                                                            @PageableDefault(page = 0, size = 10, sort = "sid") Pageable pageable) {
        Page<StockResponse> response=stockService.getAllStock(stockSize,pid,pageable);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"All Stock Fetched",response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<StockResponse>> putStockDetails(@Valid @RequestBody StockEntry stockEntry)
    {
        StockResponse response=stockService.createStock(stockEntry);
        return ResponseEntity.status(201).body(new ApiResponse<>(true,"Stock created sucessfully",response));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<StockResponse>> updateQuantity(@PathVariable("id") Long id, @Valid @RequestBody StockUpdateRequest updateRequest)
    {
        StockResponse response=stockRetryService.updateStockWithRetries(id,updateRequest);
      return ResponseEntity.status(200).body(new ApiResponse<>(true,"Stock updated sucessfully",response));
    }
    @PatchMapping("/{id}/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<StockResponse>> addAvailableQuantity(@PathVariable("id") Long id, @Valid @RequestBody StockUpdateRequest updateRequest)
    {
        StockResponse response=stockRetryService.addStockWithRetries(id,updateRequest);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Stock updated sucessfully",response));
    }

}
