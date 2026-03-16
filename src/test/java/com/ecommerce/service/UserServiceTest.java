package com.ecommerce.service;

import com.ecommerce.dto.UserCreateDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.dto.UserUpdateDTO;
import com.ecommerce.entity.User;
import com.ecommerce.entity.enums.Role;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setPassword("encoded");
        user.setRole(Role.CUSTOMER);
    }

    // REGISTER USER
    @Test
    void testRegisterUser_Success() {

        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("john");
        dto.setEmail("john@test.com");
        dto.setPassword("1234");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("john");
        savedUser.setEmail("john@test.com");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDTO response = userService.registerUser(dto);

        assertNotNull(response);
        assertEquals("john", response.getName());

        verify(userRepository).save(any(User.class));
    }

    // USER ALREADY EXISTS
    @Test
    void testRegisterUser_UserAlreadyExists() {

        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail("john@test.com");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(dto));
    }

    // GET USER
    @Test
    void testGetUserById() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(1L);

        assertEquals("john", response.getName());
    }

    // USER NOT FOUND
    @Test
    void testGetUserById_NotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    // UPDATE USER
    @Test
    void testUpdateUser() {

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("updated");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDTO response = userService.updateUser(1L, dto);

        assertNotNull(response);

        verify(userRepository).save(user);
    }

    // DELETE USER
    @Test
    void testDeleteUser() {

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

}