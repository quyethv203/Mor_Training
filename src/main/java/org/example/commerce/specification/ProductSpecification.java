package org.example.commerce.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.commerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ProductSpecification {
    public static Specification<Product> filter(String name, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if(categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if(minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if(maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
