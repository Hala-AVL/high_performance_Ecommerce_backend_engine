package com.EcommerceApp.H2NS.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Cart;
import com.EcommerceApp.H2NS.model.CartItem;
import com.EcommerceApp.H2NS.model.Order;
import com.EcommerceApp.H2NS.model.OrderItem;
import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.repository.CartRepository;
import com.EcommerceApp.H2NS.repository.OrderRepository;
import com.EcommerceApp.H2NS.repository.ProductRepository;

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

    // @Transactional  //for Atomoicity 
    public Order placeOrder(Long userId) {
        log.info(" Beginning order placement for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + userId);
        }

        BigDecimal totalAmount = cart.getTotalPrice();

        userService.deductBalance(userId, totalAmount);

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.OrderStatus.PENDING); //stay pending until the payment is done 
        order.setTotalAmount(totalAmount);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                // if stock is not enough refund the user 
                userService.addBalance(userId, totalAmount);
                throw new RuntimeException("Stock is not enough for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        order.setStatus(Order.OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        invoiceService.generateInvoice(savedOrder);

        log.info(" Order placed successfully: {} (Total: {})", savedOrder.getId(), totalAmount, userId);
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
