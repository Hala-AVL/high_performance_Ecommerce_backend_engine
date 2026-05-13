package com.EcommerceApp.H2NS.service;


import org.springframework.stereotype.Service;

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
   
    /**
     * تسجيل مستخدم جديد
     * Before: بدون أي حماية من التضارب
     */
    public User register(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("البريد الإلكتروني مستخدم بالفعل");
        }
       
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setRole(User.UserRole.CUSTOMER);
       
        User savedUser = userRepository.save(user);
       
        // إنشاء سلة تلقائياً للمستخدم الجديد
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);
       
        log.info("✅ تم تسجيل المستخدم: {}", email);
        return savedUser;
    }
   
    /**
     * تسجيل الدخول
     */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
       
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("كلمة المرور غير صحيحة");
        }
       
        log.info("🔑 تم تسجيل دخول المستخدم: {}", email);
        return user;
    }
   
    /**
     * جلب مستخدم بالـ ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
    }
}