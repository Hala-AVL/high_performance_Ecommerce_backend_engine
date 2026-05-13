package com.EcommerceApp.H2NS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcommerceApp.H2NS.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
   
    List<Product> findByActiveTrue();
   
    List<Product> findByStockQuantityLessThanEqual(Integer threshold);
}