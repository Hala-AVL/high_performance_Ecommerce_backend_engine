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

    // for race condiction - 1st non-func req 
    public Product updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Fount with ID : " + productId));

        int oldStock = product.getStockQuantity();
        int newStock = oldStock + quantity;

        //@Transactional
        product.setStockQuantity(newStock);
        Product saved = productRepository.save(product);

        log.info(" Updating inventory for {}: {} -> {}", product.getName(), oldStock, newStock);
        return saved;
    }
// for 1st non-func req 

    public Product deductStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found with ID : " + productId));

        int oldStock = product.getStockQuantity();

        if (oldStock < quantity) {
            throw new RuntimeException("Inventory insufficient");
        }

        int newStock = oldStock - quantity;
        product.setStockQuantity(newStock);
        Product saved = productRepository.save(product);

        log.info("Deducting stock for {}: {} -> {}", product.getName(), oldStock, newStock);
        return saved;
    }

    public List<Product> getAllInventory() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThanEqual(threshold);
    }

    public Integer getProductStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found with ID : " + productId));
        return product.getStockQuantity();
    }
}
