package com.ecommerce.service;

import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.entity.*;
import com.ecommerce.entity.enums.OrderStatus;
import com.ecommerce.entity.enums.PaymentStatus;
import com.ecommerce.exception.ProductOutOfStockException;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Order order;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(50000);
        product.setStock(10);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(10000.0);

        cartItem.setCart(cart);
        cart.getItems().add(cartItem);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderItems(new ArrayList<>());
    }

    // SUCCESSFUL CHECKOUT
    @Test
    void testCheckout_Success() {

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        when(orderItemRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO response = orderService.checkout(1L, PaymentStatus.SUCCESS);

        assertNotNull(response);
        assertEquals("PLACED", response.getOrderStatus());

        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(productRepository).save(any(Product.class));
        verify(emailService).sendOrderConfirmation(user.getEmail(), 1L);
        verify(cartRepository).save(cart);
    }

    // CART NOT FOUND
    @Test
    void testCheckout_CartNotFound() {

        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.checkout(1L, PaymentStatus.SUCCESS));
    }

    // EMPTY CART
    @Test
    void testCheckout_EmptyCart() {

        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.checkout(1L, PaymentStatus.SUCCESS));
    }

    // PAYMENT FAILED
    @Test
    void testCheckout_PaymentFailed() {

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        when(orderItemRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO response = orderService.checkout(1L, PaymentStatus.FAILED);

        assertEquals("CANCELLED", response.getOrderStatus());

        verify(emailService, never()).sendOrderConfirmation(any(), any());
    }

    // PRODUCT OUT OF STOCK
    @Test
    void testCheckout_ProductOutOfStock() {

        product.setStock(1); // less than quantity

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        assertThrows(ProductOutOfStockException.class,
                () -> orderService.checkout(1L, PaymentStatus.SUCCESS));
    }

    // GET USER ORDERS
    @Test
    void testGetUserOrders() {

        order.setOrderItems(new ArrayList<>());
        order.setOrderStatus(OrderStatus.PLACED);

        when(orderRepository.findByUser(user))
                .thenReturn(List.of(order));

        List<OrderResponseDTO> orders = orderService.getUserOrders(user);

        assertEquals(1, orders.size());

        verify(orderRepository).findByUser(user);
    }

    // GET ORDER BY ID

    @Test
    void testGetOrderById() {

        order.setOrderItems(new ArrayList<>());
        order.setOrderStatus(OrderStatus.PLACED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderResponseDTO response = orderService.getOrderById(1L);

        assertEquals(1L, response.getOrderId());
    }

    // ORDER NOT FOUND
    @Test
    void testGetOrderById_NotFound() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(1L));
    }

    // UPDATE ORDER STATUS
    @Test
    void testUpdateOrderStatus() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        OrderResponseDTO response = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals("SHIPPED", response.getOrderStatus());

        verify(orderRepository).save(order);
    }
}