package com.EcommerceApp.H2NS.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
   
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
   
    /**
     * عناصر الطلب
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();
   
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
   
    public enum OrderStatus {
        PENDING,      // قيد الانتظار (السلة تحولت لطلب)
        CONFIRMED,    // مؤكد (بعد الدفع)
        PROCESSING,   // قيد المعالجة
        SHIPPED,      // تم الشحن
        DELIVERED,    // تم التسليم
        CANCELLED     // ملغي
    }
   
    /**
     * إضافة عنصر للطلب
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
   
    /**
     * التحقق من إمكانية تأكيد الطلب
     */
    public boolean canBeConfirmed() {
        return status == OrderStatus.PENDING;
    }
}