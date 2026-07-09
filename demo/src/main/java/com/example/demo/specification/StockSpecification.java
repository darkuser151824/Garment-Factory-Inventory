package com.example.demo.specification;

import com.example.demo.entity.Stock;
import com.example.demo.enums.Size;
import org.springframework.data.jpa.domain.Specification;

public class StockSpecification {

    public static Specification<Stock> hasSize(Size size)
    {
        return (root,query,cb)->
        size==null?null:cb.equal(root.get("size"),size);
    }

    public static Specification<Stock> hasProduct(Long pid) {
        return (root, query, cb) ->
                pid == null ? null : cb.equal(root.get("product").get("pid"), pid);
    }

}
