package com.EcommerceApp.H2NS.controller;


import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcommerceApp.H2NS.model.Cart;
import com.EcommerceApp.H2NS.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * عرض السلة
     * GET /api/cart/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        try {
            Cart cart = cartService.getCart(userId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * إضافة منتج للسلة
     * POST /api/cart/{userId}/add
     */
    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(@PathVariable Long userId, @RequestBody Map<String, Integer> request) {
        try {
            Cart cart = cartService.addToCart(
                    userId,
                    request.get("productId").longValue(),
                    request.get("quantity")
            );
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * حذف منتج من السلة
     * DELETE /api/cart/{userId}/remove/{productId}
     */
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            Cart cart = cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * تفريغ السلة
     * DELETE /api/cart/{userId}/clear
     */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "تم تفريغ السلة"));
    }

    /**
     * إجمالي السلة
     * GET /api/cart/{userId}/total
     */
    @GetMapping("/{userId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getCartTotal(@PathVariable Long userId) {
        BigDecimal total = cartService.getCartTotal(userId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}
