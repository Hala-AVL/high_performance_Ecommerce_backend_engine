package com.EcommerceApp.H2NS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order;
   
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
   
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.GENERATED;
   
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;
   
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
   
    public enum InvoiceStatus {
        GENERATED, SENT, PAID
    }
   
    public static String generateInvoiceNumber(Long orderId) {
        return "INV-" + LocalDateTime.now().getYear() + "-" +
               String.format("%06d", orderId);
    }
}