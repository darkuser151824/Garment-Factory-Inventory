package com.example.demo.Controller;

import com.example.demo.dto.ProductEntryRequest;
import com.example.demo.dto.ProductRespsonseRequest;
import com.example.demo.entity.Product;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import com.example.demo.enums.Size;
import com.example.demo.exception.ApiResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProductRedisService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path="/api/products",produces = "application/json")
public class ProductController {
    private ProductService prodService;
    private ProductRedisService productRedisService;

    public ProductController(ProductService productService,ProductRedisService productRedisService)
    {
        this.productRedisService=productRedisService;
        this.prodService=productService;
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductRespsonseRequest>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductEntryRequest request) {

        ProductRespsonseRequest productRespsonseRequest=prodService.updateProduct(id, request);

        return ResponseEntity.status(201).body(new ApiResponse<>(true,"The Product "+id+" is updated",productRespsonseRequest));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        prodService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Page<ProductRespsonseRequest>>> getProducts(  @RequestParam(required = false) Color color,
                                                                                    @RequestParam(required = false) Fabric fabric,
                                                                                    @RequestParam(required = false) Garment garment,@PageableDefault(page = 0, size = 10, sort = "pid", direction = Sort.Direction.ASC)Pageable pageable)
    {
        Page<ProductRespsonseRequest> productRespsonseRequestList=prodService.getAllProducts(color,fabric,garment,pageable);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Products are Fetched sucesfully",productRespsonseRequestList));
    }
    @GetMapping(path = "/fabric/{fabric}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<List<ProductRespsonseRequest>>>  getByFabric(@PathVariable("fabric") Fabric fabric)
    {
        List<ProductRespsonseRequest> productRespsonseRequestList=prodService.getProductByFabric(fabric);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Product with "+fabric+" are fetched.",productRespsonseRequestList));
    }
    @GetMapping(path = "/color/{color}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<List<ProductRespsonseRequest>>>  getByColor(@PathVariable("color") Color color)
    {
        List<ProductRespsonseRequest> productRespsonseRequestList=prodService.getProductByColor(color);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Product with "+color+" fetched",productRespsonseRequestList));
    }


    @GetMapping(path="/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<ProductRespsonseRequest>> getProductById(@PathVariable("id") Long id)
    {
        ProductRespsonseRequest productRespsonseRequest=productRedisService.getProductById(id);
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Product with "+id+" Fetched successfully",productRespsonseRequest));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ApiResponse<ProductRespsonseRequest>> setProduct(@Valid @RequestBody ProductEntryRequest productEntryRequest)
    {
        ProductRespsonseRequest productRespsonseRequest=prodService.createProduct(productEntryRequest);
        return  ResponseEntity.status(201).body(new ApiResponse<>(true,"Product created",productRespsonseRequest));
    }

    @GetMapping("/meta")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> getProductMeta() {

        return ResponseEntity.status(200).body(Map.of(
                "garments", Garment.values(),
                "colors", Color.values(),
                "fabrics", Fabric.values(),
                "size", Size.values()
        ));
    }

}
