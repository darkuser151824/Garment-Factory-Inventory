package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.specification.StockSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockService {
    private StockRepository stockRepo;
    private ProductRepository prodRepo;



    public StockService(StockRepository stockRepository,ProductRepository productRepository)
    {
        this.stockRepo=stockRepository;
        this.prodRepo=productRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Stock updateStock(OrderEntry orderEntry)
    {
        log.info("Stock update called,find fired for Product:{} and Size {} ",orderEntry.getPid(),orderEntry.getSize());
//        pessimistic locking applied and aquired here
        Stock stock=stockRepo.findByPidAndSizeAndUpdate(orderEntry.getPid(),orderEntry.getSize());
        if (stock == null) {
            log.warn("Stock for for Product:{} and Size {} NOT FOUND",orderEntry.getPid(),orderEntry.getSize());
            throw new ResourceNotFoundException(
                    "Stock not found for product " + orderEntry.getPid() + " size " + orderEntry.getSize()
            );
        }
        if(stock.getAvailableQty()>=orderEntry.getQuantity())
        {
            log.debug("Stock reduced from {} to {} ",stock.getAvailableQty(),stock.getAvailableQty()- orderEntry.getQuantity());
            stock.setAvailableQty(stock.getAvailableQty()- orderEntry.getQuantity());
            stock.setAllocatedQty(stock.getAllocatedQty()+orderEntry.getQuantity());
        }else {
            log.error("Insufficient Stock for pid={}, size={}: available={}, requested={} \n" ,
                        orderEntry.getPid(), orderEntry.getSize(),
                        stock.getAvailableQty(), orderEntry.getQuantity());
            throw new InsufficientStockException("Not Enough Stocks "+stock.getAvailableQty());
        }
        Stock savedStock=stockRepo.save(stock);
        return savedStock;
    }
    @Transactional(readOnly = true)
    public Page<StockResponse> getAllStock(Size size, Long pid, Pageable pageable) {
        Specification<Stock> spec= StockSpecification.hasSize(size).and(StockSpecification.hasProduct(pid));
        log.info("getAllStock called with size={}, pid={}", size, pid);
        Page<Stock> stockPage=stockRepo.findAll(spec,pageable);

        List<Stock> stockList=stockPage.getContent();
        List<Long> productIds=stockList.stream().map(stock -> stock.getProduct().getPid()).distinct().collect(Collectors.toList());
        List<Product> productList = prodRepo.findAllById(productIds);
        Map<Long,Product> productMap=productList.stream()
                .collect(Collectors.toMap(product -> product.getPid(),p->p));
        log.debug("getAllStock returning {} stocks across {} products",
                stockList.size(), productIds.size());
        return stockPage.map(stock -> mapToEntitysrWithProduct(stock,productMap.getOrDefault(stock.getProduct().getPid(),null)));
    }
    public StockResponse mapToEntitysrWithProduct(Stock stock,Product product)
    {
        StockResponse stockResponse=new StockResponse();
        if(product==null)
        {
            throw new ResourceNotFoundException("Product was not found with pid"+stock.getProduct().getPid());
        }
        stockResponse.setQuantity(stock.getAvailableQty());
        stockResponse.setPid(product.getPid());
        stockResponse.setSize(stock.getSize());
        stockResponse.setSid(stock.getSid());
        return stockResponse;
    }
    public Stock mapToEntity(StockEntry stockEntry)
    {
        Product product=prodRepo.findById(stockEntry.getPid()).orElseThrow(()-> new ResourceNotFoundException("Product with id "+stockEntry.getPid() +" not found."));

        Stock stock=new Stock();
        stock.setProduct(product);
        stock.setSize(stockEntry.getSize());
        stock.setAvailableQty(stockEntry.getQuantity());
        return stock;
    }
    public StockResponse mapToEntitysr(Stock stock)
    {
        StockResponse stockResponse=new StockResponse();

        stockResponse.setQuantity(stock.getAvailableQty());
        stockResponse.setPid(stock.getProduct().getPid());
        stockResponse.setSize(stock.getSize());
        stockResponse.setSid(stock.getSid());
        return stockResponse;
    }


    @Transactional(readOnly = true)
    public List<StockResponse> getStockByProduct(Long pid) {
        log.info("Find Stock for Product {} called",pid);
        Product product=prodRepo.findById(pid).orElseThrow(()->new ResourceNotFoundException("The Product with this pid "+pid+" NOT FOUND."));
        List<Stock> stockList=stockRepo.findAllByProductPid(pid);
        log.debug("Stock for product {} fetched ,Number of stocks {}",pid,stockList.size());
        return stockList.stream()
                .map(stock->mapToEntitysrWithProduct(stock,product))
                .collect(Collectors.toList());
    }


    @Transactional
    public StockResponse createStock(StockEntry stockEntry)
    {
        Stock stock=mapToEntity(stockEntry);
        log.info("createStock called for pid={}, size={}, qty={}",
                stockEntry.getPid(), stockEntry.getSize(), stockEntry.getQuantity());
        stock=stockRepo.save(stock);

        return mapToEntitysr(stock);
    }

    @Transactional
    public StockResponse updateStock(Long id, StockUpdateRequest updateRequest)
    {
        log.info("updateStock called for stock{} ,update request {}",id,updateRequest.getNewQuantity());
        Stock stock=stockRepo.findById(id).orElseThrow(()-> {
            throw new ResourceNotFoundException("Stock with id "+id+" not found");});
        if(stock.getAvailableQty()>=updateRequest.getNewQuantity())
        {
            log.debug("Stock id={} quantity: {} -> {}",
                    id, stock.getAvailableQty(), stock.getAvailableQty() - updateRequest.getNewQuantity());
            stock.setAvailableQty(stock.getAvailableQty()-updateRequest.getNewQuantity());
            stock.setAllocatedQty(stock.getAllocatedQty()+updateRequest.getNewQuantity());
        }else{
            int x=(stock.getAvailableQty()-updateRequest.getNewQuantity());
            log.warn("Insufficient stock for stockId={}: available={}, requested={}",
                    id, stock.getAvailableQty(), updateRequest.getNewQuantity());
            throw new InsufficientStockException("Stock are not sufficient "+x+" stocks are required more ");
        }

        stock=stockRepo.save(stock);
        return mapToEntitysr(stock);
    }
    @Transactional
    public StockResponse addStock(Long id, StockUpdateRequest updateRequest)
    {
        log.info("addStock called for stock{} ,update request {}",id,updateRequest.getNewQuantity());
        Stock stock=stockRepo.findById(id).orElseThrow(()-> {
            throw new ResourceNotFoundException("Stock with id "+id+" not found");});
        log.debug("Stock id={} availableQty: {} -> {}",
                id, stock.getAvailableQty(), stock.getAvailableQty() + updateRequest.getNewQuantity());
        stock.setAvailableQty(stock.getAvailableQty()+updateRequest.getNewQuantity());
        stock=stockRepo.save(stock);
        log.info("addStock completed for stockId={}, newAvailableQty={}", id, stock.getAvailableQty());
        return mapToEntitysr(stock);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void createStockForProduct(ProductEntryRequest productEntryRequest, Product product)
    {
        log.info("createStockForProduct called for product={},sml {},medium {},large {}",product.getPid(),productEntryRequest.getSmallQty(),productEntryRequest.getMediumQty(),productEntryRequest.getLargeQty());
        Stock smallStock = new Stock();
        smallStock.setProduct(product);
        smallStock.setSize(Size.SMALL);
        smallStock.setAvailableQty(productEntryRequest.getSmallQty());



        Stock mediumStock = new Stock();
        mediumStock.setProduct(product);
        mediumStock.setSize(Size.MEDIUM);
        mediumStock.setAvailableQty(productEntryRequest.getMediumQty());




        Stock largeStock = new Stock();
        largeStock.setProduct(product);
        largeStock.setSize(Size.LARGE);
        largeStock.setAvailableQty(productEntryRequest.getLargeQty());

        stockRepo.saveAllAndFlush(List.of(smallStock, mediumStock, largeStock));
    }

    @Transactional
    public Map<Size,Stock> getStockForProduct(Product product)
    {
        log.info("getStockForProduct called for pid={}", product.getPid());
        List<Stock> stocks = stockRepo.findAllByProductPid(product.getPid());
        if(stocks==null)
        {
            throw new ResourceNotFoundException("Stocks are null and there is no stock for this product ");
        }
        Map<Size, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getSize, s -> s));
        if(stockMap.get(Size.SMALL)==null || stockMap.get(Size.MEDIUM)==null||stockMap.get(Size.LARGE)==null){
            throw new ResourceNotFoundException("The stock were found missing for product"+product.getPid());
        }

        return  stockMap;
    }



}
