package com.EcommerceApp.H2NS.service;

import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Order;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {
   
    /**
     * ⚠️ BEFORE: إرسال إشعار متزامن
     *
     * المشكلة: المستخدم بينتظر الإشعار ما يتبعت
     * في المرحلة التانية: حنحولها لـ @Async
     */
    public void sendOrderConfirmation(Order order) {
        log.info("📧 إرسال إشعار تأكيد الطلب رقم {} (متزامن)", order.getId());
       
        // ⚠️ محاكاة عملية بطيئة
        try {
            Thread.sleep(1000); // ثانية كاملة تأخير
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
       
        log.info("✅ تم إرسال إشعار التأكيد للطلب رقم {}", order.getId());
    }
   
    /**
     * إرسال إشعار عام
     */
    public void sendNotification(String email, String message) {
        log.info("📬 إرسال إشعار إلى {}: {}", email, message);
       
        // محاكاة إرسال
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}