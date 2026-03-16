package com.ecommerce.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailServiceTest {

    private JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);

    private EmailService emailService = new EmailService(mailSender);

    @Test
    void testSendOrderConfirmation() {
        assertDoesNotThrow(() -> emailService.sendOrderConfirmation("test@test.com", 1L));
    }
}