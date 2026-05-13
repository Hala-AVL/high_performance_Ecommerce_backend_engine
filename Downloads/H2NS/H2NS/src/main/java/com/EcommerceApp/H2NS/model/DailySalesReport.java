package com.EcommerceApp.H2NS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_sales_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesReport {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(nullable = false, unique = true)
    private LocalDate reportDate;
   
    @Column(nullable = false)
    private Integer totalOrders;
   
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSales;
   
    @Column(nullable = false)
    private Integer totalItemsSold;
   
    @Column
    private String topSellingProduct;
   
    @Column
    private Integer topSellingProductQuantity;
   
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;
   
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}