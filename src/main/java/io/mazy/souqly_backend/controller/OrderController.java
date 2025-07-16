package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.entity.Order;
import io.mazy.souqly_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Récupère toutes les commandes de l'utilisateur connecté
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(auth.getName());
            
            List<Order> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupère une commande par son ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        try {
            return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupère une commande par son numéro
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            return orderService.getOrderByNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Crée une nouvelle commande
     */
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long buyerId = Long.parseLong(auth.getName());
            
            Long productId = Long.parseLong(request.get("productId").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            String paymentMethod = request.get("paymentMethod").toString();
            
            Order order = orderService.createOrder(buyerId, productId, quantity, paymentMethod);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Met à jour le statut d'une commande
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestBody Map<String, String> request
    ) {
        try {
            String statusStr = request.get("status");
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Annule une commande
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Marque une commande comme livrée
     */
    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<Order> markAsDelivered(@PathVariable Long orderId) {
        try {
            Order order = orderService.markAsDelivered(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupère les statistiques des commandes de l'utilisateur connecté
     */
    @GetMapping("/stats")
    public ResponseEntity<OrderService.OrderStats> getOrderStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(auth.getName());
            
            OrderService.OrderStats stats = orderService.getUserOrderStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 