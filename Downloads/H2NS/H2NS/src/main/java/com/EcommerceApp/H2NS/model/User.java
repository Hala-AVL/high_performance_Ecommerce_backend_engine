package com.EcommerceApp.H2NS.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor  ;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(unique = true, nullable = false)
    private String email;
   
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) 
    // password will appaer in request but not in response 
    private String password;
   
    @Column(nullable = false)
    private String name;

    @Column(nullable = false) 
    private BigDecimal balance = new BigDecimal("1000.00") ; 
   
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;
   
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
   
    public enum UserRole {
        CUSTOMER, ADMIN
    }  
}
