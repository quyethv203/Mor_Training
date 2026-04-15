package org.example.commerce.service;

import org.example.commerce.dto.request.ProductRequest;
import org.example.commerce.dto.response.ProductResponse;
import org.example.commerce.entity.Category;
import org.example.commerce.entity.Product;
import org.example.commerce.exception.AlreadyExistedResource;
import org.example.commerce.exception.ResourceNotFoundException;
import org.example.commerce.mapper.ProductMapper;
import org.example.commerce.repository.CategoryRepository;
import org.example.commerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    public List<ProductResponse> getProductWithCategory() {
        List<Product> products = productRepository.findProductWithCategory();
        return products.stream().map(productMapper::toResponse).toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new AlreadyExistedResource("Name of the product existed!");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    public ProductResponse updateProduct(ProductRequest request, Integer id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        productMapper.updateProductFromRequest(request, existingProduct);
        existingProduct.setCategory(category);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Integer id) {
        Product existedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(existedProduct);
    }
}
