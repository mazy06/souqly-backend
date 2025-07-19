package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionTrackingService {
    
    private final UserInteractionRepository userInteractionRepository;
    private final UserRepository userRepository;
    
    /**
     * Enregistre une interaction utilisateur
     */
    public void trackInteraction(UserDto userDto, Product product, UserInteraction.InteractionType type, 
                               String value, String sessionId, String ipAddress, String userAgent) {
        try {
            // Récupérer l'entité User à partir de l'ID
            Optional<User> userOpt = userRepository.findById(userDto.getId());
            if (userOpt.isEmpty()) {
                log.error("User not found for tracking interaction: {}", userDto.getId());
                return;
            }
            
            User user = userOpt.get();
            
            UserInteraction interaction = new UserInteraction();
            interaction.setUser(user);
            interaction.setProduct(product);
            interaction.setInteractionType(type);
            interaction.setInteractionValue(value);
            interaction.setSessionId(sessionId);
            interaction.setIpAddress(ipAddress);
            interaction.setUserAgent(userAgent);
            
            userInteractionRepository.save(interaction);
            log.info("Interaction tracked: {} for user {} and product {}", type, user.getId(), product.getId());
        } catch (Exception e) {
            log.error("Error tracking interaction: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Enregistre une vue de produit
     */
    public void trackProductView(UserDto userDto, Product product, String sessionId, String ipAddress, String userAgent) {
        trackInteraction(userDto, product, UserInteraction.InteractionType.VIEW, null, sessionId, ipAddress, userAgent);
    }
    
    /**
     * Enregistre un ajout aux favoris
     */
    public void trackFavorite(UserDto userDto, Product product, String sessionId, String ipAddress, String userAgent) {
        trackInteraction(userDto, product, UserInteraction.InteractionType.FAVORITE, "added", sessionId, ipAddress, userAgent);
    }
    
    /**
     * Enregistre un retrait des favoris
     */
    public void trackUnfavorite(UserDto userDto, Product product, String sessionId, String ipAddress, String userAgent) {
        trackInteraction(userDto, product, UserInteraction.InteractionType.UNFAVORITE, "removed", sessionId, ipAddress, userAgent);
    }
    
    /**
     * Enregistre une recherche
     */
    public void trackSearch(UserDto userDto, String searchQuery, String sessionId, String ipAddress, String userAgent) {
        try {
            // Récupérer l'entité User à partir de l'ID
            Optional<User> userOpt = userRepository.findById(userDto.getId());
            if (userOpt.isEmpty()) {
                log.error("User not found for tracking search: {}", userDto.getId());
                return;
            }
            
            User user = userOpt.get();
            
            // Pour les recherches, on n'a pas de produit spécifique
            UserInteraction interaction = new UserInteraction();
            interaction.setUser(user);
            interaction.setProduct(null); // Pas de produit pour les recherches
            interaction.setInteractionType(UserInteraction.InteractionType.SEARCH);
            interaction.setInteractionValue(searchQuery);
            interaction.setSessionId(sessionId);
            interaction.setIpAddress(ipAddress);
            interaction.setUserAgent(userAgent);
            
            userInteractionRepository.save(interaction);
        } catch (Exception e) {
            log.error("Error tracking search: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les interactions d'un utilisateur
     */
    public List<UserInteraction> getUserInteractions(Long userId) {
        return userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Récupère les interactions pour un produit
     */
    public List<UserInteraction> getProductInteractions(Long productId) {
        return userInteractionRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    
    /**
     * Récupère les interactions récentes d'un utilisateur
     */
    public List<UserInteraction> getRecentUserInteractions(Long userId, int limit) {
        return userInteractionRepository.findByUserIdOrderByCreatedAtDescLimit(userId, limit);
    }
} 