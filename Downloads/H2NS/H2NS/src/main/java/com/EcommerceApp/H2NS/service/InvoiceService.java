package com.EcommerceApp.H2NS.service;


import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Invoice;
import com.EcommerceApp.H2NS.model.Order;
import com.EcommerceApp.H2NS.repository.InvoiceRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceService {
   
    private final InvoiceRepository invoiceRepository;
   
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
   
    /**
     * ⚠️ BEFORE: إنشاء فاتورة متزامنة (في نفس المسار)
     *
     * المشكلة: المستخدم بيستنى لما الفاتورة تتولد
     * ده اللي رح نحسنه في المرحلة التانية بـ @Async
     */
    public Invoice generateInvoice(Order order) {
        log.info("🧾 إنشاء فاتورة للطلب رقم {} (متزامن)", order.getId());
       
        // ⚠️ محاكاة عملية بطيئة - المستخدم مستني
        try {
            Thread.sleep(500); // نص ثانية تأخير
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
       
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(Invoice.generateInvoiceNumber(order.getId()));
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setStatus(Invoice.InvoiceStatus.GENERATED);
       
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("✅ تم إنشاء الفاتورة: {}", savedInvoice.getInvoiceNumber());
       
        return savedInvoice;
    }
   
    /**
     * جلب فاتورة للطلب
     */
    public Invoice getInvoiceByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("الفاتورة غير موجودة"));
    }
}