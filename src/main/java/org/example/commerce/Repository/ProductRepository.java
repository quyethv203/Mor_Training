package org.example.commerce.Repository;

import org.example.commerce.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAll(Pageable pageable);

    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findProductWithCategory();

    boolean existsByName(String name);
}
