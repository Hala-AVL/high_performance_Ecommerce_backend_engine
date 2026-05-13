package com.EcommerceApp.H2NS.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryService {
   
    private final ProductRepository productRepository;
   
    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
   
    /**
     * ⚠️ BEFORE: تحديث المخزون بدون أي حماية من التضارب
     *
     * ده اللي رح يسبب Race Condition في اختبار JMeter
     */
    public Product updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("المنتج غير موجود"));
       
        int oldStock = product.getStockQuantity();
        int newStock = oldStock + quantity;
       
        // ⚠️ حفظ مباشر بدون قفل وبدون @Transactional
        product.setStockQuantity(newStock);
        Product saved = productRepository.save(product);
       
        log.info("📊 تحديث المخزون: {} ({} -> {})", product.getName(), oldStock, newStock);
        return saved;
    }
   
    /**
     * خصم من المخزون - ⚠️ بدون حماية
     */
    public Product deductStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("المنتج غير موجود"));
       
        int oldStock = product.getStockQuantity();
       
        if (oldStock < quantity) {
            throw new RuntimeException("المخزون غير كافٍ");
        }
       
        int newStock = oldStock - quantity;
        product.setStockQuantity(newStock);
        Product saved = productRepository.save(product);
       
        log.info("📉 خصم من المخزون: {} ({} -> {})", product.getName(), oldStock, newStock);
        return saved;
    }
   
    /**
     * عرض المخزون الحالي
     */
    public List<Product> getAllInventory() {
        return productRepository.findByActiveTrue();
    }
   
    /**
     * المنتجات اللي قاربت على النفاذ
     */
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThanEqual(threshold);
    }
   
    /**
     * عرض مخزون منتج محدد
     */
    public Integer getProductStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("المنتج غير موجود"));
        return product.getStockQuantity();
    }
}