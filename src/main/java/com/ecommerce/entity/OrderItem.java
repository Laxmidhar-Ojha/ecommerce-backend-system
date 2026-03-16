package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private double price;

    // Each order item is associated with one order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Each order item is associated with one product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}