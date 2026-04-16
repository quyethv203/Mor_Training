package org.example.commerce.controller;

import jakarta.validation.Valid;
import org.example.commerce.dto.request.ProductRequest;
import org.example.commerce.dto.response.ApiResponse;
import org.example.commerce.dto.response.ProductResponse;
import org.example.commerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProduct(pageable);
        ApiResponse<Page<ProductResponse>> response = ApiResponse.<Page<ProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Products retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-category")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductWithCategory() {
        List<ProductResponse> products = productService.getProductWithCategory();
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Products with category retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getDetailProduct(@PathVariable Integer productId) {
        ProductResponse product = productService.getDetailProduct(productId);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Products detail retrieved successfully")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .data(product)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest request, @PathVariable Integer id) {
        ProductResponse product = productService.updateProduct(request, id);
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Product updated successfully")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Product deleted successfully")
                .build();
        return ResponseEntity.noContent().build();
    }

}
