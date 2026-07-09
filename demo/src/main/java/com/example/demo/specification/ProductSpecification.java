package com.example.demo.specification;

import com.example.demo.entity.Product;
import com.example.demo.enums.Color;
import com.example.demo.enums.Fabric;
import com.example.demo.enums.Garment;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasColor(Color color) {
        return (root, query, cb) ->
                color == null ? null : cb.equal(root.get("color"), color);
    }

    public static Specification<Product> hasFabric(Fabric fabric) {
        return (root, query, cb) ->
                fabric == null ? null : cb.equal(root.get("fabric"), fabric);
    }

    public static Specification<Product> hasGarment(Garment garment) {
        return (root, query, cb) ->
                garment == null ? null : cb.equal(root.get("garment"), garment);
    }
}