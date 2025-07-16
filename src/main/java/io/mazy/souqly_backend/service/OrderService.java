package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.Order;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.OrderRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Récupère toutes les commandes d'un utilisateur
     */
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Récupère une commande par son ID
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    /**
     * Récupère une commande par son numéro
     */
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return Optional.ofNullable(orderRepository.findByOrderNumber(orderNumber));
    }
    
    /**
     * Crée une nouvelle commande
     */
    public Order createOrder(Long buyerId, Long productId, Integer quantity, String paymentMethod) {
        User buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Acheteur non trouvé"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        User seller = product.getSeller();
        
        // Générer un numéro de commande unique
        String orderNumber = generateOrderNumber();
        
        // Calculer le prix total
        BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalPrice(totalPrice);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    /**
     * Met à jour le statut d'une commande
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    /**
     * Annule une commande
     */
    public Order cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.CANCELLED);
    }
    
    /**
     * Marque une commande comme livrée
     */
    public Order markAsDelivered(Long orderId) {
        return updateOrderStatus(orderId, Order.OrderStatus.DELIVERED);
    }
    
    /**
     * Génère un numéro de commande unique
     */
    private String generateOrderNumber() {
        String orderNumber;
        do {
            // Format: ORD-YYYYMMDD-XXXX (ex: ORD-20250715-1001)
            String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            long sequence = System.currentTimeMillis() % 10000;
            orderNumber = String.format("ORD-%s-%04d", date, sequence);
        } while (orderRepository.existsByOrderNumber(orderNumber));
        
        return orderNumber;
    }
    
    /**
     * Récupère les statistiques des commandes pour un utilisateur
     */
    public OrderStats getUserOrderStats(Long userId) {
        long totalOrders = orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.PENDING) +
                          orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.PROCESSING) +
                          orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.SHIPPED) +
                          orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.DELIVERED);
        
        long pendingOrders = orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.PENDING);
        long processingOrders = orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.PROCESSING);
        long shippedOrders = orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByBuyerIdAndStatus(userId, Order.OrderStatus.DELIVERED);
        
        return new OrderStats(totalOrders, pendingOrders, processingOrders, shippedOrders, deliveredOrders);
    }
    
    public static class OrderStats {
        private final long totalOrders;
        private final long pendingOrders;
        private final long processingOrders;
        private final long shippedOrders;
        private final long deliveredOrders;
        
        public OrderStats(long totalOrders, long pendingOrders, long processingOrders, long shippedOrders, long deliveredOrders) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.processingOrders = processingOrders;
            this.shippedOrders = shippedOrders;
            this.deliveredOrders = deliveredOrders;
        }
        
        // Getters
        public long getTotalOrders() { return totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public long getProcessingOrders() { return processingOrders; }
        public long getShippedOrders() { return shippedOrders; }
        public long getDeliveredOrders() { return deliveredOrders; }
    }
} 