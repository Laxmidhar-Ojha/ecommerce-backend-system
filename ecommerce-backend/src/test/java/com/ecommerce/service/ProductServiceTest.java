package com.ecommerce.service;

import com.ecommerce.dto.ProductRequestDTO;
import com.ecommerce.dto.ProductResponseDTO;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        product = new Product(
                1L,
                "Laptop",
                "Gaming Laptop",
                80000,
                10,
                "Electronics",
                "image-url",
                4.5);

        requestDTO = new ProductRequestDTO(
                "Laptop",
                "Gaming Laptop",
                80000,
                10,
                "Electronics",
                "image-url");
    }

    // ADD PRODUCT TEST
    @Test
    void testAddProduct() {

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.addProduct(requestDTO);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(80000, response.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    // GET PRODUCT BY ID SUCCESS
    @Test
    void testGetProductById() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());

        verify(productRepository).findById(1L);
    }

    // GET PRODUCT BY ID NOT FOUND
    @Test
    void testGetProductById_NotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    // GET ALL PRODUCTS
    @Test
    void testGetAllProducts() {

        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<ProductResponseDTO> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());

        verify(productRepository).findAll();
    }

    // UPDATE PRODUCT
    @Test
    void testUpdateProduct() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.updateProduct(1L, requestDTO);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());

        verify(productRepository).save(product);
    }

    // DELETE PRODUCT
    @Test
    void testDeleteProduct() {

        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    // PAGINATION TEST
    @Test
    void testGetProductsWithPagination() {
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<ProductResponseDTO> result = productService.getProductsWithPagination(0, 10, "id", "asc");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        verify(productRepository).findAll(any(Pageable.class));
    }
}