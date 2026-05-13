package com.EcommerceApp.H2NS.service;


import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.EcommerceApp.H2NS.model.Cart;
import com.EcommerceApp.H2NS.model.CartItem;
import com.EcommerceApp.H2NS.model.Order;
import com.EcommerceApp.H2NS.model.OrderItem;
import com.EcommerceApp.H2NS.model.Product;
import com.EcommerceApp.H2NS.repository.CartRepository;
import com.EcommerceApp.H2NS.repository.OrderItemRepository;
import com.EcommerceApp.H2NS.repository.OrderRepository;
import com.EcommerceApp.H2NS.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
   
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
   
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CartRepository cartRepository,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
    }
   
    /**
     * ⚠️ النسخة الأولية - BEFORE
     *
     * المشاكل الموجودة عشان تظهر في JMeter:
     * 1. مفيش @Transactional - العملية مش Atomic
     * 2. مفيش قفل - ممكن Race Condition
     * 3. خصم المخزون وتسجيل الطلب منفصلين
     *
     * نتائج JMeter المتوقعة:
     * - المخزون ينقص لأرقام سالبة
     * - طلبات بدون خصم مخزون
     * - خصم مخزون بدون طلب
     */
    public Order placeOrder(Long userId) {
        log.warn("⚠️ BEFORE: تنفيذ placeOrder بدون حماية");
       
        // 1. جلب السلة
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("السلة فارغة"));
       
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("السلة فارغة");
        }
       
        // 2. إنشاء الطلب
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.OrderStatus.PENDING);
       
        BigDecimal total = BigDecimal.ZERO;
       
        // 3. معالجة كل منتج - ⚠️ بدون حماية من التضارب
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("المنتج غير موجود"));
           
            // ⚠️ فحص المخزون بدون قفل
            if (product.getStockQuantity() >= cartItem.getQuantity()) {
                int oldStock = product.getStockQuantity();
                int newStock = oldStock - cartItem.getQuantity();
                product.setStockQuantity(newStock);
               
                // ⚠️ حفظ مباشر بدون Transaction
                productRepository.save(product);
               
                // إنشاء OrderItem
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPriceAtPurchase(product.getPrice());
                orderItem.setOrder(order);
                order.getItems().add(orderItem);
               
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
               
                log.info("📝 خصم {} من المنتج {} ({} -> {})",
                        cartItem.getQuantity(), product.getName(), oldStock, newStock);
            } else {
                log.warn("❌ مخزون غير كافٍ: {}", product.getName());
            }
        }
       
        // 4. تحديث الطلب
        order.setTotalAmount(total);
        order.setStatus(Order.OrderStatus.CONFIRMED);
       
        // ⚠️ حفظ الطلب بدون Transaction مع خصم المخزون
        Order savedOrder = orderRepository.save(order);
       
        // 5. تفريغ السلة
        cart.getItems().clear();
        cartRepository.save(cart);
       
        log.info("🎉 تم إنشاء الطلب رقم {} - الإجمالي: {}", savedOrder.getId(), total);
        return savedOrder;
    }
   
    /**
     * عرض طلبات المستخدم
     */
    public java.util.List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
   
    /**
     * عرض تفاصيل طلب
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
    }
}