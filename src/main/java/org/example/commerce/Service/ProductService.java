package org.example.commerce.Service;

import org.example.commerce.DTO.Request.ProductRequest;
import org.example.commerce.DTO.Response.ProductResponse;
import org.example.commerce.Entity.Category;
import org.example.commerce.Entity.Product;
import org.example.commerce.Exception.AlreadyExistedResource;
import org.example.commerce.Exception.ResourceNotFoundException;
import org.example.commerce.Mapper.ProductMapper;
import org.example.commerce.Repository.CategoryRepository;
import org.example.commerce.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Category category = categoryRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    public ProductResponse updateProduct(ProductRequest request, Integer id) {
        Product existedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        productMapper.updateProductFromRequest(request, existedProduct);
        existedProduct.setCategory(category);
        Product updatedProduct = productRepository.save(existedProduct);
        return productMapper.toResponse(updatedProduct);
    }

    public void deleteProduct(Integer id) {
        Product existedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(existedProduct);
    }
}
