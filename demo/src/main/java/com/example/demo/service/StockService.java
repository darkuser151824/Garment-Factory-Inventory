package com.example.demo.service;

import com.example.demo.dto.StockEntry;
import com.example.demo.dto.StockResponse;
import com.example.demo.dto.StockUpdateRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.awt.color.ProfileDataException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {
    private StockRepository stockRepo;
    private ProductRepository prodRepo;


    public StockService(StockRepository stockRepository,ProductRepository productRepository)
    {
        this.stockRepo=stockRepository;
        this.prodRepo=productRepository;
    }
    public Stock mapToEntity(StockEntry stockEntry)
    {
        Product product=prodRepo.findById(stockEntry.getPid()).orElseThrow(()-> new RuntimeException("Product with id "+stockEntry.getPid() +" not found."));

        Stock stock=new Stock();
        stock.setProduct(product);
        stock.setSize(stockEntry.getSize());
        stock.setQuantity(stockEntry.getQuantity());
        return stock;
    }
    public StockResponse mapToEntitysr(Stock stock)
    {
        StockResponse stockResponse=new StockResponse();
        
        stockResponse.setQuantity(stock.getQuantity());
        stockResponse.setPid(stock.getProduct().getPid());
        stockResponse.setSize(stock.getSize());
        stockResponse.setSid(stock.getSid());
        return stockResponse;
    }

    public StockResponse createStock(StockEntry stockEntry)
    {
        Stock stock=mapToEntity(stockEntry);
        stock=stockRepo.save(stock);
        return mapToEntitysr(stock);
    }

    public StockResponse updateStock(Long id, StockUpdateRequest updateRequest)
    {
        Stock stock=stockRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("This stock is not found"));

        stock.setQuantity(updateRequest.getNewQuantity());
        stock=stockRepo.save(stock);
        return mapToEntitysr(stock);
    }

    public List<StockResponse> getAllStock() {
        List<Stock> stockList=stockRepo.findAll();
        List<StockResponse> stockResponseList=new ArrayList<>();
        for(Stock stock:stockList)
        {
            stockResponseList.add(mapToEntitysr(stock));
        }
        return stockResponseList;
    }

    public List<StockResponse> getStockByProduct(Long pid) {
        Product product=prodRepo.findById(pid).orElseThrow(()->new ResourceNotFoundException("The Product with this pid "+pid+" NOT FOUND."));
        List<StockResponse> stockResponseList=new ArrayList<>();
        for(Size size:Size.values())
        {
            Stock stock=stockRepo.findByPidAndSize(product.getPid(),size);
            stockResponseList.add(mapToEntitysr(stock));

        }
        return stockResponseList;
    }
}
