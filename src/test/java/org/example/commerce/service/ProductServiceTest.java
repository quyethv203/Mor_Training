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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_validRequest_returnProductResponse() {
        ProductRequest request = new ProductRequest();
        request.setName("Iphone 15 promax");
        request.setDescription("Iphone 15 series");
        request.setStock(10);
        request.setPrice(BigDecimal.valueOf(25000000));
        request.setCategoryId(1);

        Category category = new Category();
        category.setId(1);
        category.setName("Technology");

        Product mappedProduct = new Product();
        mappedProduct.setId(1);
        mappedProduct.setName("Iphone 15 promax");
        mappedProduct.setDescription("Iphone 15 series");
        mappedProduct.setStock(10);
        mappedProduct.setPrice(BigDecimal.valueOf(25000000));
        mappedProduct.setCreatedAt(LocalDateTime.now());
        mappedProduct.setModifiedAt(LocalDateTime.now());

        ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setId(1);
        expectedResponse.setName("Iphone 15 promax");
        expectedResponse.setDescription("Iphone 15 series");
        expectedResponse.setStock(10);
        expectedResponse.setPrice(BigDecimal.valueOf(25000000));
        expectedResponse.setCategoryId(1);
        expectedResponse.setCategoryName("Technology");

        when(productRepository.existsByName("Iphone 15 promax")).thenReturn(false);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(request)).thenReturn(mappedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(mappedProduct);
        when(productMapper.toResponse(mappedProduct)).thenReturn(expectedResponse);

        ProductResponse productResponse = productService.createProduct(request);
        assertNotNull(productResponse);
        assertEquals("Iphone 15 promax", productResponse.getName());
        assertEquals(1, productResponse.getCategoryId());

        verify(productRepository, times(1)).existsByName(request.getName());
        verify(categoryRepository, times(1)).findById(request.getCategoryId());
        verify(productMapper, times(1)).toEntity(request);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toResponse(mappedProduct);
    }

    @Test
    void createProduct_duplicateName_throwAlreadyExistedResourceException() {
        ProductRequest request = new ProductRequest();
        request.setName("Iphone 15 promax");
        request.setDescription("Iphone 15 series");
        request.setStock(10);
        request.setPrice(BigDecimal.valueOf(25000000));
        request.setCategoryId(1);

        when(productRepository.existsByName(request.getName())).thenReturn(true);
        AlreadyExistedResource exception = assertThrows(
                AlreadyExistedResource.class,
                () -> productService.createProduct(request)
        );

        assertEquals("Name of the product existed!", exception.getMessage());

        verify(productRepository, times(1)).existsByName(request.getName());
        verify(categoryRepository, never()).findById(request.getCategoryId());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getDetailProduct_foundedProduct_returnProductResponse() {
        Integer productId = 1;
        Product product = new Product();
        product.setId(1);
        product.setName("Iphone 15 promax");
        product.setDescription("Iphone 15 series");
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(25000000));

        ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setId(1);
        expectedResponse.setName("Iphone 15 promax");
        expectedResponse.setDescription("Iphone 15 series");
        expectedResponse.setStock(10);
        expectedResponse.setPrice(BigDecimal.valueOf(25000000));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        ProductResponse productResponse = productService.getDetailProduct(productId);
        assertNotNull(productResponse);
        assertEquals(product.getName(), productResponse.getName());
        assertEquals(product.getId(), productResponse.getId());

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).toResponse(product);
    }

    @Test
    void getDetailProduct_notFoundedProduct_throwResourceNotFoundException() {
        Integer productId = 1;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getDetailProduct(productId)
        );

        assertEquals("Product not found with id: " + productId, ex.getMessage());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void updateProduct_foundedProduct_returnUpdatedProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Iphone 15 promax");
        request.setDescription("Iphone 15 series");
        request.setStock(15);
        request.setPrice(BigDecimal.valueOf(25000000));
        request.setCategoryId(1);

        Integer id = 1;

        Category category = new Category();
        category.setId(1);
        category.setName("Technology");

        Product existingProduct = new Product();
        existingProduct.setName("Iphone 15 promax");
        existingProduct.setDescription("Iphone 15 series");
        existingProduct.setStock(10);
        existingProduct.setPrice(BigDecimal.valueOf(25000000));
        existingProduct.setCategory(category);


        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndIdNot(request.getName(), id)).thenReturn(false);
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        doNothing().when(productMapper).updateProductFromRequest(request, existingProduct);

        ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setId(id);
        expectedResponse.setName(request.getName());
        expectedResponse.setDescription(request.getDescription());
        expectedResponse.setStock(request.getStock());
        expectedResponse.setPrice(request.getPrice());
        expectedResponse.setCategoryId(category.getId());
        expectedResponse.setCategoryName(category.getName());

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setStock(request.getStock());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(category);

        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(productMapper.toResponse(existingProduct)).thenReturn(expectedResponse);

        ProductResponse response = productService.updateProduct(request, id);
        assertNotNull(response);
        assertEquals(request.getStock(), response.getStock());

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).existsByNameAndIdNot(request.getName(), id);
        verify(categoryRepository, times(1)).findById(request.getCategoryId());
        verify(productMapper, times(1)).updateProductFromRequest(request, existingProduct);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toResponse(existingProduct);
    }

    @Test
    void deleteProduct_foundedProduct_repositoryDelete() {
        Integer id = 1;

        Product existingProduct = new Product();
        existingProduct.setName("Iphone 15 promax");
        existingProduct.setDescription("Iphone 15 series");
        existingProduct.setStock(10);
        existingProduct.setPrice(BigDecimal.valueOf(25000000));

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).delete(existingProduct);

    }

    @Test
    void deleteProduct_notFoundedProduct_throwResourceNotFoundException() {
        Integer id = 1;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(id)
        );

        assertEquals("Product not found with id: " + id, ex.getMessage());

        verify(productRepository, times(1)).findById(id);
    }

}
