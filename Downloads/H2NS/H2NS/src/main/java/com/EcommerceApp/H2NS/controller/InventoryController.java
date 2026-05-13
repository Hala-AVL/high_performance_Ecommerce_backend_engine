package com.EcommerceApp.H2NS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * عرض كل المخزون
     * GET /api/inventory
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    /**
     * عرض مخزون منتج محدد
     * GET /api/inventory/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductStock(@PathVariable Long productId) {
        try {
            Integer stock = inventoryService.getProductStock(productId);
            return ResponseEntity.ok(Map.of("productId", productId, "stock", stock));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ⚠️ تحديث المخزون (إضافة كمية) - بدون حماية
     * PUT /api/inventory/{productId}/add
     */
    @PutMapping("/{productId}/add")
    public ResponseEntity<?> addStock(@PathVariable Long productId, @RequestBody Map<String, Integer> request) {
        try {
            Product product = inventoryService.updateStock(productId, request.get("quantity"));
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ⚠️ خصم من المخزون - بدون حماية
     * PUT /api/inventory/{productId}/deduct
     */
    @PutMapping("/{productId}/deduct")
    public ResponseEntity<?> deductStock(@PathVariable Long productId, @RequestBody Map<String, Integer> request) {
        try {
            Product product = inventoryService.deductStock(productId, request.get("quantity"));
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * المنتجات القليلة المخزون
     * GET /api/inventory/low-stock?threshold=5
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStock(@RequestParam(defaultValue = "5") Integer threshold) {
        return ResponseEntity.ok(inventoryService.getLowStockProducts(threshold));
    }
}