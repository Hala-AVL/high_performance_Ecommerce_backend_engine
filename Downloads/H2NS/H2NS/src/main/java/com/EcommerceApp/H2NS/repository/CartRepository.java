package com.EcommerceApp.H2NS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcommerceApp.H2NS.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
   
    Optional<Cart> findByUserId(Long userId);
}