package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("john");
        user.setPassword("1234");
    }

    // SUCCESS CASE
    @Test
    void testLoadUserByUsername_Success() {

        when(userRepository.findByName("john"))
                .thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());

        verify(userRepository).findByName("john");
    }

    // USER NOT FOUND
    @Test
    void testLoadUserByUsername_UserNotFound() {

        when(userRepository.findByName("john"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("john"));

        verify(userRepository).findByName("john");
    }
}