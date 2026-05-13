package com.EcommerceApp.H2NS.service;

import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Order;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

    //for async non-func req 
    public void sendOrderConfirmation(Order order) {
        log.info(" Sending order confirmation notification for order ID {}: (Synchronous)", order.getId());
        //simulation for delay in notifaication sending 
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info(" Order confirmation notification sent for order ID {}", order.getId());
    }

    public void sendNotification(String email, String message) {
        log.info(" Sending notification to {}: {}", email, message);
        //simulation for delay in notifaication sending 
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
