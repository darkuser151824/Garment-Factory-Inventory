package com.example.demo.Mapper;

import com.example.demo.dto.ProductEntryRequest;
import com.example.demo.dto.ProductRespsonseRequest;
import com.example.demo.dto.StockShortResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ProductMapper {

    public ProductRespsonseRequest mapToEntityPrrForFull(Product product, List<Stock> stockList
    )
    {
        ProductRespsonseRequest prr=new ProductRespsonseRequest();
        prr.setPid(product.getPid());
        prr.setColor(product.getColor());
        prr.setFabric(product.getFabric());
        prr.setGarment(product.getGarment());
        prr.setSellingPrice(product.getSellingPrice());
        prr.setCostPerUnit(product.getCostPerUnit());
        prr.setCreatedAt(product.getCreatedAt());
        prr.setUpdatedAt(product.getUpdatedAt());
        prr.setSmallStock(toStockShortResponse(findStockBySize(stockList, Size.SMALL)));
        prr.setMediumStock(toStockShortResponse(findStockBySize(stockList, Size.MEDIUM)));
        prr.setLargeStock(toStockShortResponse(findStockBySize(stockList, Size.LARGE)));


        return prr;
    }
    private Stock findStockBySize(List<Stock> stockList, Size size) {
        return stockList.stream()
                .filter(s -> s.getSize() == size)
                .findFirst()
                .orElse(null);
    }

    private StockShortResponse toStockShortResponse(Stock stock) {
        if (stock == null) return null;
        StockShortResponse r = new StockShortResponse();
        r.setQuantity(stock.getAvailableQty());
        return r;
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
    public ProductRespsonseRequest mapToEntityPrr(Product product, Map<Size, Stock> stockMap)
    {
        ProductRespsonseRequest prr=new ProductRespsonseRequest();
        prr.setPid(product.getPid());
        prr.setColor(product.getColor());
        prr.setFabric(product.getFabric());
        prr.setGarment(product.getGarment());
        prr.setSellingPrice(product.getSellingPrice());
        prr.setCostPerUnit(product.getCostPerUnit());
        prr.setCreatedAt(product.getCreatedAt());
        prr.setUpdatedAt(product.getUpdatedAt());
        prr.setSmallStock(mapToEntitysr(stockMap.get(Size.SMALL)));
        prr.setMediumStock(mapToEntitysr(stockMap.get(Size.MEDIUM)));
        prr.setLargeStock(mapToEntitysr(stockMap.get(Size.LARGE)));

        return prr;
    }
    public StockShortResponse mapToEntitysr(Stock stock)
    {
        StockShortResponse stockShortResponse=new StockShortResponse();

        stockShortResponse.setQuantity(stock.getAvailableQty());
        stockShortResponse.setSize(stock.getSize());
        stockShortResponse.setSid(stock.getSid());
        stockShortResponse.setCreatedAt(stock.getCreatedAt());
        stockShortResponse.setUpdatedAt(stock.getUpdatedAt());
        return stockShortResponse;
    }


}
