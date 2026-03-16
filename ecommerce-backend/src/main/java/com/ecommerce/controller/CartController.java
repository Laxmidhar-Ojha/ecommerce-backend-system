package com.ecommerce.controller;

import com.ecommerce.dto.CartResponseDTO;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // Get cart of user
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(cartService.getUserCart(user.getId()));
    }

    // Add product to cart
    @PostMapping("/add/{productId}")
    public ResponseEntity<CartResponseDTO> addToCart(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int quantity) {
        String username = userDetails.getUsername();

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));

        return ResponseEntity.ok(
                cartService.addProductToCart(user, productId, quantity));
    }

    // Update cart item quantity
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(
                cartService.updateCartItem(cartItemId, quantity));
    }

    // Remove item from cart
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long cartItemId) {

        cartService.removeItemFromCart(cartItemId);

        return ResponseEntity.ok("Item removed from cart");
    }
}