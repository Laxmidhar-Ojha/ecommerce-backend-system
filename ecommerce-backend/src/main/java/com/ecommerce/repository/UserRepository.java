package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // This method finds a user by their email address, which is used for
    // authentication
    Optional<User> findByEmail(String email);

    // This method finds a user by their username, which can be used for display
    // purposes
    Optional<User> findByName(String username);

}