package com.EcommerceApp.H2NS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(nullable = false)
    private String name;
   
    @Column(length = 1000)
    private String description;
   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
   
    /**
     * كمية المخزون - حققل للتضارب (Race Condition)
     * حالياً: بدون أي حماية - معرض للتضارب
     * رح نختبر JMeter ونشوف كيف بينقص لأرقام سالبة
     */
    @Column(nullable = false)
    private Integer stockQuantity;
   
    @Column(nullable = false)
    private Boolean active = true;
   
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
   
    @Column(nullable = false)
    private LocalDateTime updatedAt;
   
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
   
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}