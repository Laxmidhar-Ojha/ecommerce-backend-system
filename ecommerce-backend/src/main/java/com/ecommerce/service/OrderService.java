package com.ecommerce.service;

import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.entity.*;
import com.ecommerce.entity.enums.OrderStatus;
import com.ecommerce.entity.enums.PaymentStatus;
import com.ecommerce.exception.ProductOutOfStockException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
        private final EmailService emailService;
        private final OrderRepository orderRepository;
        private final CartRepository cartRepository;
        private final OrderItemRepository orderItemRepository;
        private final ProductRepository productRepository;

        // Checkout (convert cart to order)
        @Transactional
        public OrderResponseDTO checkout(Long cartId, PaymentStatus paymentStatus) {

                Cart cart = cartRepository.findById(cartId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
                if (cart.getItems().isEmpty()) {
                        throw new ResourceNotFoundException("Cart is empty");
                }

                Order order = new Order();

                order.setUser(cart.getUser());
                order.setOrderDate(LocalDateTime.now());
                order.setPaymentStatus(paymentStatus);
                order.setTotalAmount(cart.getTotalPrice());
                if (paymentStatus == PaymentStatus.SUCCESS) {
                        order.setOrderStatus(OrderStatus.PLACED);
                } else {
                        order.setOrderStatus(OrderStatus.CANCELLED);
                }
                if (paymentStatus == PaymentStatus.SUCCESS) {

                        for (CartItem cartItem : cart.getItems()) {

                                Product product = cartItem.getProduct();

                                if (product.getStock() < cartItem.getQuantity()) {
                                        throw new ProductOutOfStockException(
                                                        product.getName() + " is out of stock");
                                }
                        }
                }

                Order savedOrder = orderRepository.save(order);

                List<OrderItem> orderItems = cart.getItems()
                                .stream()
                                .map(cartItem -> {

                                        OrderItem orderItem = new OrderItem();

                                        orderItem.setOrder(savedOrder);
                                        orderItem.setProduct(cartItem.getProduct());
                                        orderItem.setQuantity(cartItem.getQuantity());
                                        orderItem.setPrice(cartItem.getProduct().getPrice());

                                        return orderItem;
                                }).toList();

                orderItemRepository.saveAll(orderItems);

                savedOrder.setOrderItems(orderItems);

                // If payment SUCCESS → reduce stock + clear cart
                if (paymentStatus == PaymentStatus.SUCCESS) {
                        emailService.sendOrderConfirmation(
                                        savedOrder.getUser().getEmail(),
                                        savedOrder.getId());

                        for (CartItem cartItem : cart.getItems()) {

                                Product product = cartItem.getProduct();

                                product.setStock(product.getStock() - cartItem.getQuantity());
                                productRepository.save(product);
                        }

                        // Clear cart
                        cart.getItems().clear();
                        cart.setTotalPrice(0.0);
                        cartRepository.save(cart);
                }

                return mapToOrderResponseDTO(savedOrder);
        }

        // Get order history of user
        public List<OrderResponseDTO> getUserOrders(User user) {

                return orderRepository.findByUser(user)
                                .stream()
                                .map(this::mapToOrderResponseDTO)
                                .toList();
        }

        // Get order by id
        public OrderResponseDTO getOrderById(Long id) {

                return mapToOrderResponseDTO(orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id)));
        }

        // Update order status (Admin)
        public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

                order.setOrderStatus(status);

                return mapToOrderResponseDTO(orderRepository.save(order));
        }

        // Helper method to convert Order entity to OrderResponseDTO
        private OrderResponseDTO mapToOrderResponseDTO(Order order) {

                List<OrderItemDTO> itemDTOs = order.getOrderItems()
                                .stream()
                                .map(item -> new OrderItemDTO(
                                                item.getProduct().getId(),
                                                item.getProduct().getName(),
                                                item.getQuantity(),
                                                item.getPrice()))
                                .toList();

                return new OrderResponseDTO(
                                order.getId(),
                                order.getOrderDate(),
                                itemDTOs,
                                order.getTotalAmount(),
                                order.getOrderStatus().name());
        }
}