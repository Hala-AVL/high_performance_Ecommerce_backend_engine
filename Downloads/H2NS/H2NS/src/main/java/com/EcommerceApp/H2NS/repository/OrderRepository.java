package com.EcommerceApp.H2NS.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcommerceApp.H2NS.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   
    List<Order> findByUserId(Long userId);
   
    //for 4th non-func requirment - order history with date range and status 
    List<Order> findByStatusAndCreatedAtBetween(
            Order.OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}