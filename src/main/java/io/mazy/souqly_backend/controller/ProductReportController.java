package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.CreateReportRequest;
import io.mazy.souqly_backend.dto.ProductReportDto;
import io.mazy.souqly_backend.service.ProductReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ProductReportController {

    @Autowired
    private ProductReportService reportService;

    /**
     * Créer un nouveau signalement
     */
    @PostMapping
    public ResponseEntity<ProductReportDto> createReport(@RequestBody CreateReportRequest request) {
        try {
            ProductReportDto report = reportService.createReport(request);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer tous les signalements (pour admin)
     */
    @GetMapping
    public ResponseEntity<List<ProductReportDto>> getAllReports() {
        try {
            List<ProductReportDto> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les signalements d'un produit
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReportDto>> getProductReports(@PathVariable Long productId) {
        try {
            List<ProductReportDto> reports = reportService.getProductReports(productId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Mettre à jour le statut d'un signalement
     */
    @PutMapping("/{reportId}/status")
    public ResponseEntity<ProductReportDto> updateReportStatus(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().build();
            }
            
            ProductReportDto report = reportService.updateReportStatus(reportId, status);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un signalement
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        try {
            reportService.deleteReport(reportId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer les statistiques des signalements
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getReportStats() {
        try {
            Map<String, Object> stats = reportService.getReportStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 