package com.ecommerce.dto;

import com.ecommerce.entity.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDTO {

    private PaymentStatus paymentStatus;

}