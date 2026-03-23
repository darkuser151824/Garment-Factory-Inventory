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
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path="/api/products",produces = "application/json")
public class ProductController {
    private ProductService prodService;

    public ProductController(ProductService productService)
    {
        this.prodService=productService;
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductRespsonseRequest>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductEntryRequest request) {

        ProductRespsonseRequest productRespsonseRequest=prodService.updateProduct(id, request);

        return ResponseEntity.ok(new ApiResponse<>(true,"The Product "+id+" is updated",productRespsonseRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        prodService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductRespsonseRequest>>> getProducts()
    {
        List<ProductRespsonseRequest> productRespsonseRequestList=prodService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse<>(true,"Products are Fetched sucesfully",productRespsonseRequestList));
    }
    @GetMapping(path = "/fabric/{fabric}")
    public ResponseEntity<ApiResponse<List<ProductRespsonseRequest>>>  getByFabric(@PathVariable("fabric") Fabric fabric)
    {
        List<ProductRespsonseRequest> productRespsonseRequestList=prodService.getProductByFabric(fabric);
        return ResponseEntity.ok(new ApiResponse<>(true,"Product with "+fabric+" are fetched.",productRespsonseRequestList));
    }
    @GetMapping(path = "/color/{color}")
    public ResponseEntity<ApiResponse<List<ProductRespsonseRequest>>>  getByColor(@PathVariable("color") Color color)
    {
        List<ProductRespsonseRequest> productRespsonseRequestList=prodService.getProductByColor(color);
        return ResponseEntity.ok(new ApiResponse<>(true,"Product with "+color+" fetched",productRespsonseRequestList));
    }


    @GetMapping(path="/{id}")
    public ResponseEntity<ApiResponse<ProductRespsonseRequest>> getProductById(@PathVariable("id") Long id)
    {
        ProductRespsonseRequest productRespsonseRequest=prodService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse<>(true,"Product with "+id+" Fetched successfully",productRespsonseRequest));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ProductRespsonseRequest>> setProduct(@Valid @RequestBody ProductEntryRequest productEntryRequest)
    {
        ProductRespsonseRequest productRespsonseRequest=prodService.createProduct(productEntryRequest);
        return  ResponseEntity.ok(new ApiResponse<>(true,"Product created",productRespsonseRequest));
    }

    @GetMapping("/meta")
    public ResponseEntity<?> getProductMeta() {

        return ResponseEntity.ok(Map.of(
                "garments", Garment.values(),
                "colors", Color.values(),
                "fabrics", Fabric.values(),
                "size", Size.values()
        ));
    }

}
