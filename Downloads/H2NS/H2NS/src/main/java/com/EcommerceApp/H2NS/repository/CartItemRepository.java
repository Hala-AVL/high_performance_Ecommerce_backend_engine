package com.EcommerceApp.H2NS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcommerceApp.H2NS.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}