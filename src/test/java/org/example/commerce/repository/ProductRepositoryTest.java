package org.example.commerce.repository;

import org.example.commerce.entity.Category;
import org.example.commerce.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Đồ công nghệ");
        savedCategory = categoryRepository.save(category);
    }

    @Test
    void existsByName_savedProduct_returnTrue() {
        productRepository.save(buildProduct("Iphone 15"));
        assertTrue(productRepository.existsByName("Iphone 15"));
    }

    @Test
    void findProductWithCategory_returnProductWithCategory() {
        productRepository.save(buildProduct("Iphone 15"));
        List<Product> result = productRepository.findProductWithCategory();
        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getCategory());
        assertEquals("Đồ công nghệ", result.get(0).getCategory().getName());

    }

    private Product buildProduct(String name) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Iphone 15 series");
        product.setCategory(this.savedCategory);
        product.setPrice(BigDecimal.valueOf(25000000));
        product.setStock(10);

        return product;
    }


}
