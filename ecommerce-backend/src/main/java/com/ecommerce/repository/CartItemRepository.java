package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // This method finds a cart item by its associated cart and product
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // This method deletes all cart items associated with a specific cart
    void deleteByCart(Cart cart);

}