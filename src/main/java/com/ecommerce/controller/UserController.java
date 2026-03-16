package com.ecommerce.controller;

import com.ecommerce.dto.UserUpdateDTO;

import com.ecommerce.dto.UserCreateDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserCreateDTO dto) {

        return ResponseEntity.ok(userService.registerUser(dto));
    }

    // Login (JWT will be implemented later)
    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        return userService.verify(user);
    }

    // Get User
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO dto) {

        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    // Delete User (Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");
    }
}