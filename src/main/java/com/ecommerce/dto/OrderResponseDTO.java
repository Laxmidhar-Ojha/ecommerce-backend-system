package com.ecommerce.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long orderId;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
    private double totalAmount;
    private String orderStatus;
}