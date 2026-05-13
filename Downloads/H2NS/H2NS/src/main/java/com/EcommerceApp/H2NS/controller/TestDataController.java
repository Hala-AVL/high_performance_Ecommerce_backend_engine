package com.EcommerceApp.H2NS.controller;


import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.model.User;
import com.EcommerceApp.H2NS.service.CartService;
import com.EcommerceApp.H2NS.service.ProductService;
import com.EcommerceApp.H2NS.service.UserService;

/**
* Controller مساعد لتجهيز بيانات اختبار JMeter بسرعة
*/
@RestController
@RequestMapping("/api/test")
public class TestDataController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    public TestDataController(UserService userService,
                              ProductService productService,
                              CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    /**
     * ⭐ تجهيز سيناريو اختبار Race Condition
     * POST /api/test/setup-race-condition
     *
     * بيعمل: مستخدم + منتج كميته 10 + إضافة المنتج للسلة
     * بعدين تقدر تختبر 100 طلب متزامن على placeOrder
     */
    @PostMapping("/setup-race-condition")
    public ResponseEntity<?> setupRaceCondition() {
        // إنشاء مستخدم
        User user = userService.register("test@test.com", "123456", "مستخدم اختبار");
       
        // إنشاء منتج بكمية 10 بس
        Product product = productService.addProduct(
                "منتج اختبار التضارب",
                "منتج كميته 10 للاختبار",
                new BigDecimal("100.00"),
                10
        );
       
        // إضافة المنتج للسلة بكمية 1
        cartService.addToCart(user.getId(), product.getId(), 1);
       
        return ResponseEntity.ok(Map.of(
                "message", "تم تجهيز بيانات الاختبار",
                "userId", user.getId(),
                "productId", product.getId(),
                "productStock", product.getStockQuantity(),
                "testCommand", "شغل JMeter على: POST /api/orders/place/" + user.getId()
        ));
    }

    /**
     * فحص المخزون الحالي
     * GET /api/test/check-stock/{productId}
     */
    @GetMapping("/check-stock/{productId}")
    public ResponseEntity<?> checkStock(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(Map.of(
                "productId", product.getId(),
                "productName", product.getName(),
                "currentStock", product.getStockQuantity(),
                "status", product.getStockQuantity() < 0 ? "⚠️ خلل - المخزون سالب" : "✅ طبيعي"
        ));
    }

    /**
     * إنشاء 100 مستخدم للاختبار
     * POST /api/test/create-test-users
     */
    @PostMapping("/create-test-users")
    public ResponseEntity<?> createTestUsers() {
        for (int i = 1; i <= 100; i++) {
            userService.register("user" + i + "@test.com", "pass" + i, "مستخدم " + i);
        }
        return ResponseEntity.ok(Map.of("message", "تم إنشاء 100 مستخدم"));
    }
}