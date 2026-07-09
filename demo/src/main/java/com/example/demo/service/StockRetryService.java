package com.example.demo.service;

import com.example.demo.dto.StockResponse;
import com.example.demo.dto.StockUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockRetryService {

    private StockService stockService;
    StockRetryService(StockService stockService)
    {
        this.stockService=stockService;
    }
    public StockResponse updateStockWithRetries(Long id, StockUpdateRequest updateRequest)
    {
        int maxAttempt=3;
        for(int attempt=1;attempt<=maxAttempt;attempt++)
        {
            try{
                log.info("updating stock from StockRetryService ATTempt {} ",attempt);
                return stockService.updateStock(id,updateRequest);
            }catch(ObjectOptimisticLockingFailureException e)
            {
                if(attempt<maxAttempt)
                {
                    try{
                        log.warn("Optmisitic failure for stock {} on ,attempt {} ",id,attempt);
                        Thread.sleep(50L*attempt);
                    }catch(InterruptedException ie)
                    {
                        Thread.currentThread().interrupt();
                    }
                }else{
                    log.error("updating stock failed after attempts {}",attempt);
                    throw new RuntimeException("THe number of attmepts "+maxAttempt+" is reached .pls retry after some time");
                }
            }
        }
        throw new RuntimeException("Unreachable");
    }
    public StockResponse addStockWithRetries(Long id, StockUpdateRequest updateRequest)
    {
        int maxAttempt=3;
        for(int attempt=1;attempt<=maxAttempt;attempt++)
        {
            try{
                log.info("adding stock from StockRetryService ATTempt {} ",attempt);
                return stockService.addStock(id,updateRequest);
            }catch(ObjectOptimisticLockingFailureException e)
            {
                if(attempt<maxAttempt)
                {
                    try{
                        log.warn("Optmisitic failure for stock {} on ,attempt {} ",id,attempt);
                        Thread.sleep(50L*attempt);
                    }catch(InterruptedException ie)
                    {
                        Thread.currentThread().interrupt();
                    }
                }else{
                    log.error("adding stock failed after attempts {}",attempt);
                    throw new RuntimeException("THe number of attmepts "+maxAttempt+" is reached .pls retry after some time");
                }
            }
        }
        throw new RuntimeException("Unreachable");
    }
}
