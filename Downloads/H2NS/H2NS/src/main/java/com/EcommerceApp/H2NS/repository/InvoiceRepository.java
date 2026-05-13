package com.EcommerceApp.H2NS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcommerceApp.H2NS.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
   
    Optional<Invoice> findByOrderId(Long orderId);
}