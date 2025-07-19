package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.UserSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSimilarityRepository extends JpaRepository<UserSimilarity, Long> {
    
    /**
     * Trouve la similarité entre deux utilisateurs
     */
    Optional<UserSimilarity> findByUserId1AndUserId2(Long userId1, Long userId2);
    
    /**
     * Trouve toutes les similarités pour un utilisateur, triées par score décroissant
     */
    List<UserSimilarity> findByUserId1OrderBySimilarityScoreDesc(Long userId1);
    
    /**
     * Trouve les similarités au-dessus d'un seuil pour un utilisateur
     */
    @Query("SELECT us FROM UserSimilarity us WHERE us.userId1 = :userId AND us.similarityScore > :threshold ORDER BY us.similarityScore DESC")
    List<UserSimilarity> findByUserId1AndSimilarityScoreGreaterThan(@Param("userId") Long userId, @Param("threshold") Double threshold);
    
    /**
     * Trouve les utilisateurs les plus similaires pour un utilisateur donné
     */
    @Query("SELECT us FROM UserSimilarity us WHERE us.userId1 = :userId ORDER BY us.similarityScore DESC")
    List<UserSimilarity> findTopSimilarUsers(@Param("userId") Long userId);
} 