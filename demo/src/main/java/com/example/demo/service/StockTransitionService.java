package com.example.demo.service;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Status;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockTransitionService {

    private StockRepository stockRepository;
    private CacheManager cacheManager;
    StockTransitionService(CacheManager cacheManager,StockRepository stockRepository){
        this.stockRepository=stockRepository;
        this.cacheManager=cacheManager;
    }
//    Evict site 1
    public void onOrderPlaced(List<OrderItem> orderItemList){
        for(OrderItem oi:orderItemList){
            Stock stock=stockRepository.findByPidAndSizeAndUpdate(oi.getProduct().getPid(),oi.getSize());
            if(stock==null){
                throw new ResourceNotFoundException("Stock not found for "+"oid "+oi.getOrder().getOrderId()+"product "+oi.getProduct().getPid()+" and size "+oi.getSize());
            }
            if((stock.getAvailableQty()<oi.getQuantity())){
                throw new InsufficientStockException("Stock is not sufficinet for "+stock.getSid()+" Stock = "+stock.getSid()+" Requirement = "+oi.getQuantity());
            }
            int x=oi.getQuantity();
            stock.setAvailableQty(stock.getAvailableQty()-oi.getQuantity());
            stock.setAllocatedQty(stock.getAllocatedQty()+oi.getQuantity());

            log.debug("Stock transition onOrderPlaced: pid={}, size={}, available {} -> {}, allocated {} -> {}",
                    oi.getProduct().getPid(), oi.getSize(),
                    stock.getAvailableQty(), stock.getAvailableQty() - oi.getQuantity(),
                    stock.getAllocatedQty(), stock.getAllocatedQty() + oi.getQuantity());
            stockRepository.save(stock);
            cacheManager.getCache("products").evict(oi.getProduct().getPid());
        }

    }
    public void onInProduction(List<OrderItem> orderItemList){
        for(OrderItem oi:orderItemList){
            Stock stock=stockRepository.findByPidAndSizeAndUpdate(oi.getProduct().getPid(),oi.getSize());
            if(stock==null){
                throw new ResourceNotFoundException("Stock not found for "+"oid "+oi.getOrder().getOrderId()+"product "+oi.getProduct().getPid()+" and size "+oi.getSize());
            }
            if((stock.getAllocatedQty()<oi.getQuantity())){
                throw new InsufficientStockException("Stock is not sufficinet for "+stock.getSid()+" Stock = "+stock.getAllocatedQty()+" Requirement = "+oi.getQuantity());
            }
            stock.setAllocatedQty(stock.getAllocatedQty()-oi.getQuantity());
            stock.setInProductionQty(stock.getInProductionQty()+oi.getQuantity());
            stockRepository.save(stock);
        }
    }
    public void onOrderReady(List<OrderItem> orderItemList){
        for(OrderItem oi:orderItemList){
            Stock stock=stockRepository.findByPidAndSizeAndUpdate(oi.getProduct().getPid(),oi.getSize());
            if(stock==null){
                throw new ResourceNotFoundException("Stock not found for "+"oid "+oi.getOrder().getOrderId()+"product "+oi.getProduct().getPid()+" and size "+oi.getSize());
            }
            if((stock.getInProductionQty()<oi.getQuantity())){
                throw new InsufficientStockException("Stock is not sufficinet for "+stock.getSid()+" Stock = "+stock.getInProductionQty()+" Requirement = "+oi.getQuantity());
            }
            stock.setInProductionQty(stock.getInProductionQty()-oi.getQuantity());
            stock.setReadyQty(stock.getReadyQty()+oi.getQuantity());
            stockRepository.save(stock);
        }
    }
    public void onOrderDelivered(List<OrderItem> orderItemList){
        for(OrderItem oi:orderItemList){
            Stock stock=stockRepository.findByPidAndSizeAndUpdate(oi.getProduct().getPid(),oi.getSize());
            if(stock==null){
                throw new ResourceNotFoundException("Stock not found for "+"oid "+oi.getOrder().getOrderId()+"product "+oi.getProduct().getPid()+" and size "+oi.getSize());
            }
            if((stock.getReadyQty()<oi.getQuantity())){
                throw new InsufficientStockException("Stock is not sufficient for "+stock.getSid()+" Stock = "+stock.getReadyQty()+" Requirement = "+oi.getQuantity());
            }
            stock.setReadyQty(stock.getReadyQty()-oi.getQuantity());
            stock.setDispatchedQty(stock.getDispatchedQty()+oi.getQuantity());
            stockRepository.save(stock);
        }
    }
//    evict 2
    public void onOrderCancelled(List<OrderItem> orderItemList, Status previous){
        for(OrderItem oi:orderItemList){
            Stock stock=stockRepository.findByPidAndSizeAndUpdate(oi.getProduct().getPid(),oi.getSize());
            if(stock==null){
                throw new ResourceNotFoundException("Stock not found for "+"oid "+oi.getOrder().getOrderId()+"product "+oi.getProduct().getPid()+" and size "+oi.getSize());
            }

            if(previous==Status.ORDERED){
                if(stock.getAllocatedQty()<oi.getQuantity()){
                    throw new InsufficientStockException("Stock is not sufficient for Stock"+stock.getAllocatedQty()+" Requirement"+oi.getQuantity());
                }
                stock.setAllocatedQty(stock.getAllocatedQty()-oi.getQuantity());
                stock.setAvailableQty(stock.getAvailableQty()+oi.getQuantity());
                cacheManager.getCache("products").evict(oi.getProduct().getPid());
            }else if(previous==Status.IN_PRODUCTION){
                if(stock.getInProductionQty()<oi.getQuantity()){
                    throw new InsufficientStockException("Stock is not sufficient for Stock"+stock.getInProductionQty()+" Requirement"+oi.getQuantity());
                }
                stock.setInProductionQty(stock.getInProductionQty()-oi.getQuantity());
                stock.setAvailableQty(stock.getAvailableQty()+oi.getQuantity());
                cacheManager.getCache("products").evict(oi.getProduct().getPid());
            }

            stockRepository.save(stock);
        }
    }
}
