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

@RestController
@RequestMapping("/api/test")
public class TestDataController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private static Long sharedProductId = null;

    public TestDataController(UserService userService,
            ProductService productService,
            CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    @PostMapping("/setup-race-condition")
    public ResponseEntity<?> setupRaceCondition() {
        // user واحد بس للتبسيط
        User user = userService.register("test@gmail.com", "123456", "test user");

        Product product = productService.addProduct(
                "product for race condition",
                "product quantity = 10 for testing ",
                new BigDecimal("100.00"),
                10
        );

        cartService.addToCart(user.getId(), product.getId(), 1);

        return ResponseEntity.ok(Map.of(
                "message", "race condition test data initialized",
                "userId", user.getId(),
                "productId", product.getId(),
                "productStock", product.getStockQuantity(),
                "testCommand", "run this on JMeter : POST /api/orders/place/" + user.getId()
        ));
    }

    @GetMapping("/check-stock/{productId}")
    public ResponseEntity<?> checkStock(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(Map.of(
                "productId", product.getId(),
                "productName", product.getName(),
                "currentStock", product.getStockQuantity(),
                "status", product.getStockQuantity() < 0 ? "Warning : negative stockQuantity" : " Normal stockQuantity"
        ));
    }

    @PostMapping("/create-test-users")
    public ResponseEntity<?> createTestUsers() {
        for (int i = 1; i <= 100; i++) {
            userService.register("user" + i + "@test.com", "pass" + i, "user " + i);
        }
        return ResponseEntity.ok(Map.of("message", "100 test users created"));
    }

    @PostMapping("/setup-user-with-cart")
    public ResponseEntity<?> setupUserWithCart() {
        User user = userService.register(
                "user" + System.currentTimeMillis() + "@gmail.com",
                "123456",
                "test user with cart"
        );
        if (sharedProductId == null) {
            Product sharedProduct = productService.addProduct(
                    " PRODUCT FOR RACE CONDITION TEST ",
                    "This product will be shared by all users to cause race condition",
                    new BigDecimal("100.00"),
                    50 
            );
            sharedProductId = sharedProduct.getId();
        }
        cartService.addToCart(user.getId(), sharedProductId, 1);

        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "productId", sharedProductId,
                "productStock", "SHARED (50 total for all users)",
                "message", "ready for testing at: /api/orders/place/" + user.getId()
        ));
    }

}
