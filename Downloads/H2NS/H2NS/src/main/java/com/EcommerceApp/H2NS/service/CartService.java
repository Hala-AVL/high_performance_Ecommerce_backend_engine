package com.EcommerceApp.H2NS.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Cart;
import com.EcommerceApp.H2NS.model.CartItem;
import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.repository.CartRepository;
import com.EcommerceApp.H2NS.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Inventory insufficient");
        }

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cart.addItem(cartItem);
        Cart savedCart = cartRepository.save(cart);

        log.info(" Added {} units of {} to user {}'s cart",
                quantity, product.getName(), userId);
        return savedCart;
    }

    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        Cart savedCart = cartRepository.save(cart);

        log.info(" Removed product {} from user {}'s cart", productId, userId);
        return savedCart;
    }

    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
        log.info(" Cleared cart for user {}", userId);
    }

    public BigDecimal getCartTotal(Long userId) {
        Cart cart = getCart(userId);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            total = total.add(
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        return total;
    }
}
