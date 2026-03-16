package com.ecommerce.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender mailSender;

    // This method sends an order confirmation email to the specified recipient with
    // the order ID
    public void sendOrderConfirmation(String to, Long orderId) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject("Order Confirmation");
        message.setText("Your order #" + orderId + " has been successfully placed.");

        mailSender.send(message);
    }
}