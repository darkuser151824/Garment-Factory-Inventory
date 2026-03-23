package com.example.demo.service;

import com.example.demo.dto.ProductEntryRequest;
import com.example.demo.dto.ProductRespsonseRequest;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Size;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private ProductRepository productRepo;
    private StockRepository stockRepo;

    public ProductService(ProductRepository productRepository,StockRepository stockRepository)
    {
        this.productRepo=productRepository;
        this.stockRepo=stockRepository;
    }
    public ProductRespsonseRequest createProduct(ProductEntryRequest productEntryRequest)
    {
        Product product=mapToEntity(productEntryRequest);
        Product savedProduct=productRepo.save(product);
        ProductRespsonseRequest prr=mapToEntityPrr(savedProduct);
        createStockForProduct(productEntryRequest,savedProduct,prr);
        return prr;
    }
    public void createStockForProduct(ProductEntryRequest productEntryRequest,Product product,ProductRespsonseRequest prr)
    {
        Stock smallStock = new Stock();
        smallStock.setProduct(product);
        smallStock.setSize(Size.SMALL);
        smallStock.setQuantity(productEntryRequest.getSmallQty());
        Stock ss=stockRepo.save(smallStock);

        prr.setSmallStock(ss);


        Stock mediumStock = new Stock();
        mediumStock.setProduct(product);
        mediumStock.setSize(Size.MEDIUM);
        mediumStock.setQuantity(productEntryRequest.getMediumQty());
        Stock ms=stockRepo.save(mediumStock);
        prr.setMediumStock(ms);


        Stock largeStock = new Stock();
        largeStock.setProduct(product);
        largeStock.setSize(Size.LARGE);
        largeStock.setQuantity(productEntryRequest.getLargeQty());
        Stock ls=stockRepo.save(largeStock);
        prr.setLargeStock(ls);
        
    }

    public Product mapToEntity(ProductEntryRequest per)
    {
        Product product=new Product();

        product.setColor(per.getColor());
        product.setFabric(per.getFabric());
        product.setGarment(per.getGarment());
        product.setSellingPrice(per.getSellingPrice());
        product.setCostPerUnit(per.getCostPerUnit());

        return product;
    }
    public ProductRespsonseRequest mapToEntityPrr(Product product)
    {
        ProductRespsonseRequest prr=new ProductRespsonseRequest();
        prr.setPid(product.getPid());
        prr.setColor(product.getColor());
        prr.setFabric(product.getFabric());
        prr.setGarment(product.getGarment());
        prr.setSellingPrice(product.getSellingPrice());
        prr.setCostPerUnit(product.getCostPerUnit());

        Stock smallStock=stockRepo.findByPidAndSize(product.getPid(),Size.SMALL);
        prr.setSmallStock(smallStock);
        Stock mediumStock=stockRepo.findByPidAndSize(product.getPid(),Size.MEDIUM);
        prr.setMediumStock(mediumStock);
        Stock largeStock=stockRepo.findByPidAndSize(product.getPid(),Size.LARGE);
        prr.setLargeStock(largeStock);

        return prr;
    }


    public List<ProductRespsonseRequest> getAllProducts() {
        List<Product> list=productRepo.findAll();
        List<ProductRespsonseRequest> productRespsonseRequestslist=new ArrayList<>();
        for(Product product:list)
        {
            productRespsonseRequestslist.add(mapToEntityPrr(product));
        }
        return productRespsonseRequestslist;
    }

    public ProductRespsonseRequest getProductById(Long id) {
        Product product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product with id "+id+" NOt found."));
        ProductRespsonseRequest productRespsonseRequest=mapToEntityPrr(product);
        return productRespsonseRequest;

    }

    public List<ProductRespsonseRequest> getProductByColor(Color color) {
        List<Product> productList=productRepo.getProductByColor(color);
        List<ProductRespsonseRequest> productRespsonseRequestList=new ArrayList<>();
        for(Product product:productList)
        {
            productRespsonseRequestList.add(mapToEntityPrr(product));
        }
        return productRespsonseRequestList;
    }

    public List<ProductRespsonseRequest> getProductByFabric(Fabric fabric) {
        List<Product> productList=productRepo.getProductByFabric(fabric);
        List<ProductRespsonseRequest> productRespsonseRequestList=new ArrayList<>();
        for(Product product:productList)
        {
            productRespsonseRequestList.add(mapToEntityPrr(product));
        }
        return productRespsonseRequestList;
    }

    public ProductRespsonseRequest updateProduct(Long id, ProductEntryRequest request) {
        Product product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("THe PRoduct with id "+id+" Could not be found."));

        product.setGarment(request.getGarment());
        product.setColor(request.getColor());
        product.setFabric(request.getFabric());
        product.setSellingPrice(request.getSellingPrice());
        product.setCostPerUnit(request.getCostPerUnit());

        productRepo.save(product);

        return mapToEntityPrr(product);
    }
    @Transactional
    public void deleteProduct(Long id) {

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

}
