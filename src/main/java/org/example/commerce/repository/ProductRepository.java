package org.example.commerce.repository;

import org.example.commerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findProductWithCategory();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
