package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeRecommendationService {
    
    private final UserInteractionRepository userInteractionRepository;
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AdvancedMLService advancedMLService;
    
    // Cache pour les recommandations en temps réel
    private final Map<Long, List<Map<String, Object>>> realTimeCache = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> lastUpdateTime = new ConcurrentHashMap<>();
    
    // Scheduler pour les mises à jour en temps réel
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /**
     * Initialise le service de recommandations en temps réel
     */
    public void initializeRealTimeService() {
        log.info("Initialisation du service de recommandations en temps réel...");
        
        // Planifier les mises à jour périodiques
        scheduler.scheduleAtFixedRate(this::updateAllRealTimeRecommendations, 0, 5, TimeUnit.MINUTES);
        
        // Planifier le nettoyage du cache
        scheduler.scheduleAtFixedRate(this::cleanupCache, 0, 30, TimeUnit.MINUTES);
        
        log.info("Service de recommandations en temps réel initialisé");
    }
    
    /**
     * Génère des recommandations en temps réel pour un utilisateur
     */
    public List<Map<String, Object>> getRealTimeRecommendations(Long userId, int limit) {
        try {
            // Vérifier le cache
            List<Map<String, Object>> cachedRecommendations = realTimeCache.get(userId);
            LocalDateTime lastUpdate = lastUpdateTime.get(userId);
            
            // Si le cache est récent (moins de 2 minutes), l'utiliser
            if (cachedRecommendations != null && lastUpdate != null && 
                lastUpdate.isAfter(LocalDateTime.now().minusMinutes(2))) {
                log.debug("Utilisation du cache pour l'utilisateur {}", userId);
                return cachedRecommendations.stream().limit(limit).collect(Collectors.toList());
            }
            
            // Générer de nouvelles recommandations
            List<Map<String, Object>> recommendations = generateRealTimeRecommendations(userId, limit);
            
            // Mettre en cache
            realTimeCache.put(userId, recommendations);
            lastUpdateTime.put(userId, LocalDateTime.now());
            
            // Envoyer via WebSocket
            sendRealTimeRecommendations(userId, recommendations);
            
            return recommendations;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations temps réel: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Met à jour les recommandations en temps réel pour tous les utilisateurs actifs
     */
    private void updateAllRealTimeRecommendations() {
        try {
            log.debug("Mise à jour des recommandations temps réel pour tous les utilisateurs...");
            
            // Récupérer les utilisateurs actifs (simulation)
            List<Long> activeUsers = getActiveUsers();
            
            for (Long userId : activeUsers) {
                try {
                    List<Map<String, Object>> recommendations = generateRealTimeRecommendations(userId, 10);
                    realTimeCache.put(userId, recommendations);
                    lastUpdateTime.put(userId, LocalDateTime.now());
                    
                    // Envoyer via WebSocket
                    sendRealTimeRecommendations(userId, recommendations);
                    
                } catch (Exception e) {
                    log.error("Erreur lors de la mise à jour pour l'utilisateur {}: {}", userId, e.getMessage());
                }
            }
            
            log.debug("Mise à jour terminée pour {} utilisateurs", activeUsers.size());
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour globale: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Génère des recommandations en temps réel basées sur les interactions récentes
     */
    private List<Map<String, Object>> generateRealTimeRecommendations(Long userId, int limit) {
        try {
            // Récupérer les interactions récentes (dernières 24h)
            LocalDateTime recentTime = LocalDateTime.now().minusHours(24);
            List<UserInteraction> recentInteractions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Filtrer les interactions récentes
            List<UserInteraction> filteredInteractions = recentInteractions.stream()
                .filter(interaction -> interaction.getCreatedAt().isAfter(recentTime))
                .collect(Collectors.toList());
            
            // Analyser les patterns en temps réel
            Map<String, Double> realTimePreferences = analyzeRealTimePreferences(filteredInteractions);
            
            // Générer des recommandations basées sur les préférences temps réel
            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            for (int i = 0; i < limit; i++) {
                Map<String, Object> recommendation = new HashMap<>();
                
                // Score temps réel
                double realTimeScore = calculateRealTimeScore(realTimePreferences, i);
                
                // Facteurs temps réel
                Map<String, Object> factors = new HashMap<>();
                factors.put("recentInteractions", filteredInteractions.size());
                factors.put("preferenceStrength", realTimePreferences.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                factors.put("timeDecay", calculateTimeDecay(filteredInteractions));
                factors.put("trendingScore", calculateTrendingScore(i));
                
                recommendation.put("productId", 1000 + i);
                recommendation.put("score", realTimeScore);
                recommendation.put("algorithm", "real-time");
                recommendation.put("timestamp", LocalDateTime.now());
                recommendation.put("factors", factors);
                recommendation.put("freshness", calculateFreshnessScore(i));
                
                recommendations.add(recommendation);
            }
            
            // Trier par score
            recommendations.sort((a, b) -> Double.compare(
                (Double) b.get("score"), 
                (Double) a.get("score")
            ));
            
            return recommendations;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations temps réel: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Envoie les recommandations via WebSocket
     */
    private void sendRealTimeRecommendations(Long userId, List<Map<String, Object>> recommendations) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "REAL_TIME_RECOMMENDATIONS");
            message.put("userId", userId);
            message.put("recommendations", recommendations);
            message.put("timestamp", LocalDateTime.now());
            
            // Envoyer au canal utilisateur spécifique
            messagingTemplate.convertAndSend("/user/" + userId + "/recommendations", message);
            
            log.debug("Recommandations temps réel envoyées à l'utilisateur {}", userId);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi des recommandations temps réel: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Traite une nouvelle interaction en temps réel
     */
    public void processRealTimeInteraction(UserInteraction interaction) {
        try {
            Long userId = interaction.getUser().getId();
            
            // Invalider le cache pour cet utilisateur
            realTimeCache.remove(userId);
            lastUpdateTime.remove(userId);
            
            // Générer de nouvelles recommandations immédiatement
            List<Map<String, Object>> newRecommendations = generateRealTimeRecommendations(userId, 5);
            
            // Mettre en cache
            realTimeCache.put(userId, newRecommendations);
            lastUpdateTime.put(userId, LocalDateTime.now());
            
            // Envoyer les nouvelles recommandations
            sendRealTimeRecommendations(userId, newRecommendations);
            
            log.info("Interaction temps réel traitée pour l'utilisateur {}", userId);
            
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'interaction temps réel: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Nettoie le cache périodiquement
     */
    private void cleanupCache() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);
            
            // Supprimer les entrées expirées
            lastUpdateTime.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));
            realTimeCache.entrySet().removeIf(entry -> !lastUpdateTime.containsKey(entry.getKey()));
            
            log.debug("Cache nettoyé. {} entrées restantes", realTimeCache.size());
            
        } catch (Exception e) {
            log.error("Erreur lors du nettoyage du cache: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Obtient les utilisateurs actifs (simulation)
     */
    private List<Long> getActiveUsers() {
        // Simulation d'utilisateurs actifs
        List<Long> activeUsers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            if (Math.random() > 0.3) { // 70% de chance d'être actif
                activeUsers.add((long) i);
            }
        }
        return activeUsers;
    }
    
    /**
     * Analyse les préférences en temps réel
     */
    private Map<String, Double> analyzeRealTimePreferences(List<UserInteraction> interactions) {
        Map<String, Double> preferences = new HashMap<>();
        
        // Préférences basées sur les interactions récentes
        preferences.put("electronics", 0.3 + Math.random() * 0.4);
        preferences.put("fashion", 0.2 + Math.random() * 0.5);
        preferences.put("home", 0.4 + Math.random() * 0.3);
        preferences.put("sports", 0.1 + Math.random() * 0.6);
        
        // Ajuster selon les interactions récentes
        if (!interactions.isEmpty()) {
            double interactionFactor = Math.min(interactions.size() / 10.0, 1.0);
            preferences.replaceAll((k, v) -> v * (1 + interactionFactor * 0.2));
        }
        
        return preferences;
    }
    
    /**
     * Calcule le score temps réel
     */
    private double calculateRealTimeScore(Map<String, Double> preferences, int index) {
        double baseScore = preferences.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.5);
        double timeFactor = 0.8 + (Math.random() * 0.4); // 0.8-1.2
        double freshnessFactor = 1.0 - (index * 0.05); // Décroissance par position
        
        return baseScore * timeFactor * freshnessFactor;
    }
    
    /**
     * Calcule le facteur de décroissance temporelle
     */
    private double calculateTimeDecay(List<UserInteraction> interactions) {
        if (interactions.isEmpty()) return 1.0;
        
        LocalDateTime now = LocalDateTime.now();
        double avgAge = interactions.stream()
            .mapToDouble(interaction -> 
                java.time.Duration.between(interaction.getCreatedAt(), now).toHours())
            .average()
            .orElse(24.0);
        
        // Décroissance exponentielle
        return Math.exp(-avgAge / 24.0);
    }
    
    /**
     * Calcule le score de tendance
     */
    private double calculateTrendingScore(int index) {
        return 0.7 + Math.random() * 0.3 - (index * 0.02);
    }
    
    /**
     * Calcule le score de fraîcheur
     */
    private double calculateFreshnessScore(int index) {
        return 1.0 - (index * 0.1);
    }
    
    /**
     * Arrête le service
     */
    public void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            log.info("Service de recommandations temps réel arrêté");
        } catch (InterruptedException e) {
            log.error("Erreur lors de l'arrêt du service: {}", e.getMessage(), e);
            scheduler.shutdownNow();
        }
    }
} 