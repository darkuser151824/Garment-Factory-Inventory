package com.example.demo.service;

import com.example.demo.Mapper.ProductMapper;
import com.example.demo.dto.ProductEntryRequest;
import com.example.demo.dto.ProductRespsonseRequest;
import com.example.demo.dto.StockResponse;
import com.example.demo.dto.StockShortResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import com.example.demo.enums.Size;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.specification.ProductSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private ProductRepository productRepo;
    private StockRepository stockRepo;
    private StockService stockService;
    private ProductMapper productMapper;
    private CacheManager cacheManager;


    public ProductService(CacheManager cacheManager,ProductMapper productMapper,ProductRepository productRepository,StockRepository stockRepository,StockService stockService)
    {
        this.cacheManager=cacheManager;
        this.productMapper=productMapper;
        this.productRepo=productRepository;
        this.stockRepo=stockRepository;
        this.stockService=stockService;
    }
    @Transactional(readOnly = true)
    public Page<ProductRespsonseRequest> getAllProducts(Color color,Fabric fabric, Garment garment, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.hasColor(color)
                .and(ProductSpecification.hasFabric(fabric))
                .and(ProductSpecification.hasGarment(garment));

        log.info("Get all products called for page size {}",pageable.getPageSize());

        Page<Product> productsPage = productRepo.findAll(spec, pageable);

        List<Product> productList = productsPage.getContent();

        List<Long> productIds = productList.stream()
                .map(Product::getPid)
                .collect(Collectors.toList());

        List<Stock> stockList = stockRepo.findStocksByProductPids(productIds);

        Map<Long, List<Stock>> stockMap = stockList.stream()
                .collect(Collectors.groupingBy(stock -> stock.getProduct().getPid()));

        log.info("Found {} products matching to the query ",productList.size());
        return productsPage.map(
                product -> {
                    return productMapper.mapToEntityPrrForFull(product,stockMap.getOrDefault(product.getPid(),List.of()));
                }
        );
    }




    @Transactional(readOnly = true)
    public ProductRespsonseRequest getProductById(Long id) {
        Cache cache=cacheManager.getCache("products");
        ProductRespsonseRequest cached=cache.get(id,ProductRespsonseRequest.class);
        if(cached!=null){
            return cached;
        }
        log.info("Get product by id called for product {} ",id);
        Product product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product with id "+id+" NOt found."));

        Map<Size, Stock>  stockMap=stockService.getStockForProduct(product);
        ProductRespsonseRequest productRespsonseRequest=productMapper.mapToEntityPrr(product,stockMap);
        log.info("Product {} fetched successfully ",id);
        cache.put(id,productRespsonseRequest);
        return productRespsonseRequest;
    }



    @Transactional
    public ProductRespsonseRequest createProduct(ProductEntryRequest productEntryRequest)
    {
        log.info("create product called for {} color {} Fabric {}",productEntryRequest.getGarment(),productEntryRequest.getColor(),productEntryRequest.getFabric());
        Product product=productMapper.mapToEntity(productEntryRequest);
        Product savedProduct=productRepo.save(product);
        stockService.createStockForProduct(productEntryRequest,savedProduct);
        Map<Size, Stock>  stockMap=stockService.getStockForProduct(savedProduct);
        ProductRespsonseRequest prr=productMapper.mapToEntityPrr(savedProduct,stockMap);
        log.info("Product {} created succesfully with  {} color {} Fabric {}",product.getPid(),productEntryRequest.getGarment(),productEntryRequest.getColor(),productEntryRequest.getFabric());
        return prr;
    }



    @CacheEvict(value = "products",key="#id")
    @Transactional
    public ProductRespsonseRequest updateProduct(Long id, ProductEntryRequest request) {
        log.info("Update product called for product {} ",id);
        Product product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("THe PRoduct with id "+id+" Could not be found."));

        product.setGarment(request.getGarment());
        product.setColor(request.getColor());
        product.setFabric(request.getFabric());
        product.setSellingPrice(request.getSellingPrice());
        product.setCostPerUnit(request.getCostPerUnit());

        Product savedProduct=productRepo.save(product);

        Map<Size, Stock> stockMap=stockService.getStockForProduct(savedProduct);
        log.info("Changes are made to product {} successfully ",product.getPid());
        return productMapper.mapToEntityPrr(savedProduct,stockMap);
    }
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void deleteProduct(Long id) {

//          TODO Block 4: convert to soft delete — set isDeleted=true instead of hard delete
//        Hard delete breaks referential integrity if orders reference this product

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product with id " + id + " not found"));

        // Step 1: Delete related stock
        List<Stock> stocks = stockRepo.findByProduct(product);
        stockRepo.deleteAll(stocks);

//        Order order=new Order();
//        order.getOrderItemList().


        // Step 2: Delete product
        productRepo.delete(product);
    }
    @Transactional(readOnly = true)
    public List<ProductRespsonseRequest> getProductByColor(Color color) {
        List<Product> productList=productRepo.getProductByColor(color);
        List<ProductRespsonseRequest> productRespsonseRequestList=new ArrayList<>();
        for(Product product:productList)
        {
            Map<Size, Stock>  stockMap=stockService.getStockForProduct(product);
            productRespsonseRequestList.add(productMapper.mapToEntityPrr(product,stockMap));
        }
        return productRespsonseRequestList;
    }

    @Transactional(readOnly = true)
    public List<ProductRespsonseRequest> getProductByFabric(Fabric fabric) {
        List<Product> productList=productRepo.getProductByFabric(fabric);
        List<ProductRespsonseRequest> productRespsonseRequestList=new ArrayList<>();
        for(Product product:productList)
        {
            Map<Size, Stock>  stockMap=stockService.getStockForProduct(product);
            productRespsonseRequestList.add(productMapper.mapToEntityPrr(product,stockMap));
        }
        return productRespsonseRequestList;
    }

}
