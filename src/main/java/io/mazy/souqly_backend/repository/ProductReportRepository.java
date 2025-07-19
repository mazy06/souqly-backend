package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ProductReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReportRepository extends JpaRepository<ProductReport, Long> {
    
    /**
     * Trouver tous les signalements d'un produit
     */
    List<ProductReport> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    /**
     * Trouver tous les signalements d'un utilisateur
     */
    List<ProductReport> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Trouver tous les signalements par statut
     */
    List<ProductReport> findByStatusOrderByCreatedAtDesc(ProductReport.ReportStatus status);
    
    /**
     * Compter les signalements d'un produit
     */
    long countByProductId(Long productId);
    
    /**
     * Compter les signalements par statut
     */
    long countByStatus(ProductReport.ReportStatus status);
    
    /**
     * Trouver les produits avec le plus de signalements
     */
    @Query("SELECT pr.product.id, COUNT(pr) as reportCount FROM ProductReport pr " +
           "GROUP BY pr.product.id ORDER BY reportCount DESC")
    List<Object[]> findProductsWithMostReports();
    
    /**
     * Trouver les signalements récents (derniers 30 jours)
     */
    @Query("SELECT pr FROM ProductReport pr WHERE pr.createdAt >= :startDate ORDER BY pr.createdAt DESC")
    List<ProductReport> findRecentReports(@Param("startDate") java.time.LocalDateTime startDate);
    
    /**
     * Trouver les signalements avec leurs détails pour l'admin
     */
    @Query("SELECT pr FROM ProductReport pr " +
           "LEFT JOIN FETCH pr.product p " +
           "LEFT JOIN FETCH pr.user u " +
           "ORDER BY pr.createdAt DESC")
    List<ProductReport> findAllWithDetails();
    
    /**
     * Trouver les signalements d'un produit avec détails
     */
    @Query("SELECT pr FROM ProductReport pr " +
           "LEFT JOIN FETCH pr.product p " +
           "LEFT JOIN FETCH pr.user u " +
           "WHERE pr.product.id = :productId " +
           "ORDER BY pr.createdAt DESC")
    List<ProductReport> findByProductIdWithDetails(@Param("productId") Long productId);
} 