package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Trouve le profil d'un utilisateur par son ID
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Vérifie si un utilisateur a un profil
     */
    boolean existsByUserId(Long userId);
} 