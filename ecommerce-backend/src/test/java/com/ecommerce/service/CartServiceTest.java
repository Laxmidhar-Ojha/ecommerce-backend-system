package com.ecommerce.service;

import com.ecommerce.dto.CartResponseDTO;
import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("john");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(50000);
        product.setStock(10);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart.getItems().add(cartItem);
    }

    // GET USER CART SUCCESS
    @Test
    void testGetUserCart() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        CartResponseDTO response = cartService.getUserCart(1L);

        assertNotNull(response);
        assertEquals(cart.getId(), response.getCartId());
        assertEquals(1, response.getItems().size());

        verify(userRepository).findById(1L);
        verify(cartRepository).findByUser(user);
    }

    // USER NOT FOUND
    @Test
    void testGetUserCart_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.getUserCart(1L));
    }

    @Test
    void testAddProductToCart_NewCart() {

        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setUser(user);
        newCart.setItems(new ArrayList<>());

        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(any(), any()))
                .thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartResponseDTO response = cartService.addProductToCart(user, 1L, 2);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        // verify atleast one save call to cart and cart item repositories
        verify(cartRepository, atLeast(1)).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
    }

    // ADD PRODUCT ALREADY IN CART (INCREASE QUANTITY)
    @Test
    void testAddProductToCart_ExistingItem() {

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.of(cartItem));

        cartService.addProductToCart(user, 1L, 2);

        assertEquals(4, cartItem.getQuantity());

        verify(cartItemRepository).save(cartItem);
    }

    // UPDATE CART ITEM
    @Test
    void testUpdateCartItem() {

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any())).thenReturn(cartItem);

        CartResponseDTO response = cartService.updateCartItem(1L, 5);

        assertEquals(5, cartItem.getQuantity());
        assertNotNull(response);

        verify(cartItemRepository).save(cartItem);
    }

    // UPDATE CART ITEM NOT FOUND
    @Test
    void testUpdateCartItem_NotFound() {

        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.updateCartItem(1L, 3));
    }

    // REMOVE ITEM FROM CART
    @Test
    void testRemoveItemFromCart() {

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart(1L);

        verify(cartItemRepository).delete(cartItem);
    }

    // REMOVE ITEM NOT FOUND
    @Test
    void testRemoveItemFromCart_NotFound() {

        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.removeItemFromCart(1L));
    }

    // INVALID QUANTITY
    @Test
    void testAddProduct_InvalidQuantity() {

        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProductToCart(user, 1L, 0));
    }
}