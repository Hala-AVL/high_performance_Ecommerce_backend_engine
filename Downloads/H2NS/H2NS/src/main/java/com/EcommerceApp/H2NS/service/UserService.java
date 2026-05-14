package com.EcommerceApp.H2NS.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EcommerceApp.H2NS.model.Cart;
import com.EcommerceApp.H2NS.model.User;
import com.EcommerceApp.H2NS.repository.CartRepository;
import com.EcommerceApp.H2NS.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public UserService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    // without conncurrent Access & Data Integrity 
    public User register(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Warning : Email already in use ");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setRole(User.UserRole.CUSTOMER);

        User savedUser = userRepository.save(user);

        // create empty cart for the user 
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        log.info("user registered successfully : {}", email);
        return savedUser;
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found with email:" + email));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password for user: " + email);
        }

        log.info(" User logged in successfully: {}", email);
        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found with ID: " + userId));
    }

    public BigDecimal getBalance(Long userId) {
        User user = getUserById(userId);
        return user.getBalance();
    }

    @Transactional
    public void deductBalance(Long userId, BigDecimal amount) {
        User user = getUserById(userId);

        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("balance is not sufficient, available: " + user.getBalance() + ", required: " + amount);
        }

        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);

        log.info(" balance deducted successfully from user {}: remaining balance: {}",
                userId, user.getBalance());
    }

    @Transactional
    public void addBalance(Long userId, BigDecimal amount) {
        User user = getUserById(userId);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        log.info(" balance added successfully to user {}: new balance: {}",
                userId, user.getBalance());
    }

}
