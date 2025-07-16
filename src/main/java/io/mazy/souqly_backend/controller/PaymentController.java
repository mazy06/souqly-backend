package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.PaymentMethodDto;
import io.mazy.souqly_backend.dto.PurchaseRequestDto;
import io.mazy.souqly_backend.dto.PurchaseResponseDto;
import io.mazy.souqly_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Récupère les méthodes de paiement de l'utilisateur
     */
    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethods(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<PaymentMethodDto> methods = paymentService.getPaymentMethods(userId);
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Ajoute une nouvelle méthode de paiement
     */
    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethodDto> addPaymentMethod(
            @RequestBody PaymentMethodDto paymentMethod,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            PaymentMethodDto newMethod = paymentService.addPaymentMethod(userId, paymentMethod);
            return ResponseEntity.ok(newMethod);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprime une méthode de paiement
     */
    @DeleteMapping("/payment-methods/{methodId}")
    public ResponseEntity<Void> removePaymentMethod(
            @PathVariable String methodId,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            paymentService.removePaymentMethod(userId, methodId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Effectue un achat
     */
    @PostMapping("/payments/purchase")
    public ResponseEntity<PurchaseResponseDto> purchase(
            @RequestBody PurchaseRequestDto purchaseRequest,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            PurchaseResponseDto response = paymentService.processPurchase(userId, purchaseRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère l'historique des transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<PurchaseResponseDto>> getTransactionHistory(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<PurchaseResponseDto> transactions = paymentService.getTransactionHistory(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Vérifie le solde du portefeuille
     */
    @GetMapping("/wallet/balance")
    public ResponseEntity<Double> getWalletBalance(Authentication authentication) {
        try {
            String userId = authentication.getName();
            Double balance = paymentService.getWalletBalance(userId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 