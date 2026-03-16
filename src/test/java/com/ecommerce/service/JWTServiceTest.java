package com.ecommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        jwtService = new JWTService();

        userDetails = User.builder()
                .username("john")
                .password("123")
                .roles("USER")
                .build();
    }

    // GENERATE TOKEN
    @Test
    void testGenerateToken() {

        String token = jwtService.generateToken(userDetails.getUsername());

        assertNotNull(token);
    }

    // EXTRACT USERNAME
    @Test
    void testExtractUsername() {

        String token = jwtService.generateToken(userDetails.getUsername());

        String username = jwtService.extractUsername(token);

        assertEquals("john", username);
    }

    // VALID TOKEN
    @Test
    void testValidateToken() {

        String token = jwtService.generateToken(userDetails.getUsername());

        boolean valid = jwtService.validateToken(token, userDetails);

        assertTrue(valid);
    }

    // INVALID TOKEN
    @Test
    void testValidateToken_InvalidUser() {

        String token = jwtService.generateToken(userDetails.getUsername());

        UserDetails otherUser = User.builder()
                .username("admin")
                .password("123")
                .roles("ADMIN")
                .build();

        boolean valid = jwtService.validateToken(token, otherUser);

        assertFalse(valid);
    }
}