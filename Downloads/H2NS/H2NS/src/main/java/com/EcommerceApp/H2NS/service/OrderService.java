package com.EcommerceApp.H2NS.service;

import java.math.BigDecimal;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Cart;
import com.EcommerceApp.H2NS.model.CartItem;
import com.EcommerceApp.H2NS.model.Order;
import com.EcommerceApp.H2NS.model.OrderItem;
import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.model.User;
import com.EcommerceApp.H2NS.repository.CartRepository;
import com.EcommerceApp.H2NS.repository.OrderRepository;
import com.EcommerceApp.H2NS.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final InvoiceService invoiceService;

    public OrderService(OrderRepository orderRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            UserService userService,
            InvoiceService invoiceService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.invoiceService = invoiceService;
    }

    @Transactional
    public Order placeOrder(Long userId) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return placeOrderWithLock(userId);
            } catch (OptimisticLockingFailureException e) {
                attempt++;
                log.warn(" Optimistic lock failed for user {}, retry {}/{}", userId, attempt, maxRetries);
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Failed to place order after " + maxRetries + " attempts due to concurrent modifications. Please try again later.");
                }
            }
        }
        throw new RuntimeException("Failed to place order after " + maxRetries + " attempts due to concurrent modifications. Please try again later.");
    }

    private Order placeOrderWithLock(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(" Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException(" Cart is empty for user: " + userId);
        }

        BigDecimal totalAmount = cart.getTotalPrice();
        User user = userService.getUserById(userId);
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient balance. Available: " + user.getBalance() + ", Required: " + totalAmount);
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            int updatedRows = productRepository.decrementStock(product.getId(), cartItem.getQuantity());
            if (updatedRows == 0) {
                throw new OptimisticLockingFailureException("Failed to update inventory for product: " + product.getName());
            }

            Product refreshedProduct = productRepository.findById(product.getId()).get();

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(refreshedProduct);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(refreshedProduct.getPrice());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }
        userService.deductBalance(userId, totalAmount);

        order.setStatus(Order.OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        invoiceService.generateInvoice(savedOrder);

        log.info("Order placed successfully: {} (Total: {})", savedOrder.getId(), totalAmount);
        return savedOrder;
    }

    public java.util.List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}