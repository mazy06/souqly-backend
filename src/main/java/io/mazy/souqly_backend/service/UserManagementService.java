package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.dto.UserActionRequest;
import io.mazy.souqly_backend.dto.UserStatsResponse;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.UserRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
 

@Service
@Transactional
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Récupérer tous les utilisateurs avec filtres et pagination
     */
    public Page<UserDto> getAllUsers(Pageable pageable) {
        System.out.println("🔍 UserManagementService.getAllUsers - page: " + pageable.getPageNumber() + ", size: " + pageable.getPageSize());
        
        // Utiliser directement findAll sans filtres
        Page<User> allUsers = userRepository.findAll(pageable);
        return allUsers.map(user -> {
            // Compter les produits de l'utilisateur
            int productsCount = productRepository.countBySellerId(user.getId());
            
            // Note moyenne (hardcodée pour l'instant)
            double rating = 5.0;
            
            return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.isBanned() ? "BANNED" : (user.isEnabled() ? "ACTIVE" : "SUSPENDED"),
                user.getCreatedAt(),
                user.getLastLoginAt(),
                productsCount,
                rating
            );
        });
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        int productsCount = productRepository.countBySellerId(user.getId());
        double rating = 5.0; // Hardcodé pour l'instant
        
        return new UserDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().name(),
            user.isBanned() ? "BANNED" : (user.isEnabled() ? "ACTIVE" : "SUSPENDED"),
            user.getCreatedAt(),
            user.getLastLoginAt(),
            productsCount,
            rating
        );
    }

    /**
     * Effectuer une action sur un utilisateur
     */
    public UserDto performUserAction(Long userId, UserActionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        switch (request.getAction().toLowerCase()) {
            case "suspend":
                user.setEnabled(false);
                break;
            case "ban":
                user.setEnabled(false);
                user.setBanned(true);
                break;
            case "activate":
                user.setEnabled(true);
                break;
            case "promote":
                if (user.getRole() == User.UserRole.USER) {
                    user.setRole(User.UserRole.MODERATOR);
                } else if (user.getRole() == User.UserRole.MODERATOR) {
                    user.setRole(User.UserRole.ADMIN);
                }
                break;
            case "demote":
                if (user.getRole() == User.UserRole.ADMIN) {
                    user.setRole(User.UserRole.MODERATOR);
                } else if (user.getRole() == User.UserRole.MODERATOR) {
                    user.setRole(User.UserRole.USER);
                }
                break;
            default:
                throw new RuntimeException("Action non reconnue");
        }
        
        userRepository.save(user);
        return getUser(userId);
    }

    /**
     * Mettre à jour le statut d'un utilisateur
     */
    public UserDto updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        switch (status.toUpperCase()) {
            case "ACTIVE":
                user.setEnabled(true);
                user.setBanned(false);
                break;
            case "SUSPENDED":
                user.setEnabled(false);
                user.setBanned(false);
                break;
            case "BANNED":
                user.setEnabled(false);
                user.setBanned(true);
                break;
            default:
                throw new RuntimeException("Statut non reconnu");
        }
        
        userRepository.save(user);
        return getUser(userId);
    }

    /**
     * Mettre à jour le rôle d'un utilisateur
     */
    public UserDto updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        switch (role.toUpperCase()) {
            case "USER":
                user.setRole(User.UserRole.USER);
                break;
            case "MODERATOR":
                user.setRole(User.UserRole.MODERATOR);
                break;
            case "ADMIN":
                user.setRole(User.UserRole.ADMIN);
                break;
            default:
                throw new RuntimeException("Rôle non reconnu");
        }
        
        userRepository.save(user);
        return getUser(userId);
    }

    /**
     * Récupérer les statistiques des utilisateurs
     */
    public UserStatsResponse getUserStats() {
        long total = userRepository.count();
        long active = userRepository.countByEnabledTrue();
        long suspended = userRepository.countByEnabledFalse();
        long banned = userRepository.countByBannedTrue();
        
        Map<String, Long> byRole = new HashMap<>();
        byRole.put("USER", userRepository.countByRole(User.UserRole.USER));
        byRole.put("MODERATOR", userRepository.countByRole(User.UserRole.MODERATOR));
        byRole.put("ADMIN", userRepository.countByRole(User.UserRole.ADMIN));
        
        return new UserStatsResponse(total, active, suspended, banned, byRole);
    }

    /**
     * Supprimer un utilisateur
     */
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier qu'il n'y a pas de produits associés
        if (productRepository.countBySellerId(userId) > 0) {
            throw new RuntimeException("Impossible de supprimer un utilisateur avec des produits");
        }
        
        userRepository.delete(user);
    }
    
    /**
     * Créer un modérateur (admin seulement)
     */
    public UserDto createModerator(String email) {
        // Vérifier que l'utilisateur existe déjà
        User existingUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec cet email"));
        
        // Vérifier que l'utilisateur n'est pas déjà modérateur ou admin
        if (existingUser.getRole() == User.UserRole.MODERATOR || existingUser.getRole() == User.UserRole.ADMIN) {
            throw new RuntimeException("L'utilisateur est déjà modérateur ou admin");
        }
        
        // Promouvoir en modérateur
        existingUser.setRole(User.UserRole.MODERATOR);
        User savedUser = userRepository.save(existingUser);
        
        return getUser(savedUser.getId());
    }
} 