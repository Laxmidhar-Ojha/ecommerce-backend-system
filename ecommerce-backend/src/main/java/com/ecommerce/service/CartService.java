package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CartResponseDTO;
import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Get Cart of User
    public CartResponseDTO getUserCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElse(null);

        if (cart == null) {
            return new CartResponseDTO(); // empty cart
        }

        return mapToDTO(cart);
    }

    // Add product to cart
    public CartResponseDTO addProductToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        CartItem cartItem;

        if (existingItem.isPresent()) {

            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);

        } else {

            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        updateCartTotal(cart);

        return mapToDTO(cart);
    }

    // Update quantity
    public CartResponseDTO updateCartItem(Long cartItemId, int quantity) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();

        updateCartTotal(cart);

        return mapToDTO(cartItem.getCart());
    }

    // Remove item from cart
    public void removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        Cart cart = item.getCart();

        cartItemRepository.delete(item);

        updateCartTotal(cart);
    }

    private CartResponseDTO mapToDTO(Cart cart) {

        CartResponseDTO dto = new CartResponseDTO();

        dto.setCartId(cart.getId());
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        dto.setTotalPrice(total);
        // map cart items to DTOs
        List<CartItemDTO> items = cart.getItems().stream().map(item -> {

            CartItemDTO itemDTO = new CartItemDTO();

            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setPrice(item.getProduct().getPrice());
            itemDTO.setQuantity(item.getQuantity());

            return itemDTO;

        }).toList();

        dto.setItems(items);

        return dto;
    }

    private void updateCartTotal(Cart cart) {

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);

        cartRepository.save(cart);
    }

}