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
    //for async non-func req 

    public Invoice generateInvoice(Order order) {
        log.info("Generating invoice for order: {}", order.getId());
        //simulation for delay in generating invoice 
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(Invoice.generateInvoiceNumber(order.getId()));
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setStatus(Invoice.InvoiceStatus.GENERATED);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info(" Invoice generated Successfully: {}", savedInvoice.getInvoiceNumber());

        return savedInvoice;
    }

    public Invoice getInvoiceByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for order ID: " + orderId));
    }
}
