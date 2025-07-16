package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.PaymentMethodDto;
import io.mazy.souqly_backend.dto.PurchaseRequestDto;
import io.mazy.souqly_backend.dto.PurchaseResponseDto;
import io.mazy.souqly_backend.dto.PaymentIntentDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    /**
     * Récupère les méthodes de paiement de l'utilisateur
     */
    public List<PaymentMethodDto> getPaymentMethods(String userId) {
        // Simulation des méthodes de paiement
        List<PaymentMethodDto> methods = new ArrayList<>();
        
        PaymentMethodDto wallet = new PaymentMethodDto();
        wallet.setId("wallet-" + userId);
        wallet.setType("wallet");
        wallet.setName("Portefeuille Souqly");
        wallet.setDefault(true);
        methods.add(wallet);
        
        PaymentMethodDto card = new PaymentMethodDto();
        card.setId("card-" + userId + "-1");
        card.setType("card");
        card.setName("Carte Visa");
        card.setLast4("4242");
        card.setBrand("visa");
        card.setDefault(false);
        methods.add(card);
        
        return methods;
    }

    /**
     * Ajoute une nouvelle méthode de paiement
     */
    public PaymentMethodDto addPaymentMethod(String userId, PaymentMethodDto paymentMethod) {
        // Simulation de l'ajout d'une méthode de paiement
        paymentMethod.setId("card-" + userId + "-" + System.currentTimeMillis());
        return paymentMethod;
    }

    /**
     * Supprime une méthode de paiement
     */
    public void removePaymentMethod(String userId, String methodId) {
        // Simulation de la suppression
        // En production, on supprimerait de la base de données
    }

    /**
     * Traite un achat
     */
    public PurchaseResponseDto processPurchase(String userId, PurchaseRequestDto purchaseRequest) {
        PurchaseResponseDto response = new PurchaseResponseDto();
        
        // Simulation d'un achat réussi
        response.setSuccess(true);
        response.setTransactionId("txn_" + UUID.randomUUID().toString().substring(0, 8));
        response.setMessage("Achat effectué avec succès");
        
        // Créer un PaymentIntent simulé
        PaymentIntentDto paymentIntent = new PaymentIntentDto();
        paymentIntent.setId("pi_" + UUID.randomUUID().toString().substring(0, 8));
        paymentIntent.setAmount(purchaseRequest.getAmount());
        paymentIntent.setCurrency("EUR");
        paymentIntent.setStatus("completed");
        paymentIntent.setPaymentMethodId(purchaseRequest.getPaymentMethodId());
        paymentIntent.setCreatedAt(LocalDateTime.now());
        paymentIntent.setUpdatedAt(LocalDateTime.now());
        
        response.setPaymentIntent(paymentIntent);
        
        return response;
    }

    /**
     * Récupère l'historique des transactions
     */
    public List<PurchaseResponseDto> getTransactionHistory(String userId) {
        List<PurchaseResponseDto> transactions = new ArrayList<>();
        
        // Simulation de transactions
        PurchaseResponseDto transaction1 = new PurchaseResponseDto();
        transaction1.setSuccess(true);
        transaction1.setTransactionId("txn_1");
        transaction1.setMessage("Achat iPhone 13 Pro");
        
        PaymentIntentDto paymentIntent1 = new PaymentIntentDto();
        paymentIntent1.setId("pi_1");
        paymentIntent1.setAmount(150.0);
        paymentIntent1.setCurrency("EUR");
        paymentIntent1.setStatus("completed");
        paymentIntent1.setCreatedAt(LocalDateTime.now().minusDays(1));
        paymentIntent1.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        transaction1.setPaymentIntent(paymentIntent1);
        transactions.add(transaction1);
        
        return transactions;
    }

    /**
     * Récupère le solde du portefeuille
     */
    public Double getWalletBalance(String userId) {
        // Simulation d'un solde
        return 1000.0;
    }
} 