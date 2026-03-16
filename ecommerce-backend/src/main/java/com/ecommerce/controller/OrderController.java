package com.ecommerce.controller;

import com.ecommerce.dto.CartResponseDTO;
import com.ecommerce.dto.CheckoutRequestDTO;
import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.entity.User;
import com.ecommerce.entity.enums.OrderStatus;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserRepository userRepository;

    // Checkout
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckoutRequestDTO request) {
        String username = userDetails.getUsername();

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));

        CartResponseDTO cart = cartService.getUserCart(user.getId());

        return ResponseEntity.ok(
                orderService.checkout(cart.getCartId(), request.getPaymentStatus()));
    }

    // Get orders of user
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    // Get order by id
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Update order status (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}