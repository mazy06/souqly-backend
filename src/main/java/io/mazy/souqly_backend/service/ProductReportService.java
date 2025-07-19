package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.CreateReportRequest;
import io.mazy.souqly_backend.dto.ProductReportDto;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.ProductReport;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.ProductReportRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductReportService {

    @Autowired
    private ProductReportRepository reportRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Créer un nouveau signalement
     */
    public ProductReportDto createReport(CreateReportRequest request) {
        // Vérifier que le produit existe
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créer le signalement
        ProductReport report = new ProductReport();
        report.setProduct(product);
        report.setUser(user);
        report.setReasons(request.getReasons());
        report.setCustomReason(request.getCustomReason());
        report.setDescription(request.getDescription());
        report.setStatus(ProductReport.ReportStatus.PENDING);

        ProductReport savedReport = reportRepository.save(report);
        return convertToDto(savedReport);
    }

    /**
     * Récupérer tous les signalements (pour admin)
     */
    public List<ProductReportDto> getAllReports() {
        List<ProductReport> reports = reportRepository.findAllWithDetails();
        return reports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les signalements d'un produit
     */
    public List<ProductReportDto> getProductReports(Long productId) {
        List<ProductReport> reports = reportRepository.findByProductIdWithDetails(productId);
        return reports.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour le statut d'un signalement
     */
    public ProductReportDto updateReportStatus(Long reportId, String status) {
        ProductReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));

        ProductReport.ReportStatus newStatus = ProductReport.ReportStatus.valueOf(status.toUpperCase());
        report.setStatus(newStatus);

        ProductReport updatedReport = reportRepository.save(report);
        return convertToDto(updatedReport);
    }

    /**
     * Supprimer un signalement
     */
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new RuntimeException("Signalement non trouvé");
        }
        reportRepository.deleteById(reportId);
    }

    /**
     * Récupérer les statistiques des signalements
     */
    public Map<String, Object> getReportStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques générales
        stats.put("total", reportRepository.count());
        stats.put("pending", reportRepository.countByStatus(ProductReport.ReportStatus.PENDING));
        stats.put("reviewed", reportRepository.countByStatus(ProductReport.ReportStatus.REVIEWED));
        stats.put("resolved", reportRepository.countByStatus(ProductReport.ReportStatus.RESOLVED));

        // Statistiques par raison
        Map<String, Long> byReason = new HashMap<>();
        List<ProductReport> allReports = reportRepository.findAll();
        
        for (ProductReport report : allReports) {
            for (String reason : report.getReasons()) {
                byReason.put(reason, byReason.getOrDefault(reason, 0L) + 1);
            }
        }
        stats.put("byReason", byReason);

        return stats;
    }

    /**
     * Convertir une entité en DTO
     */
    private ProductReportDto convertToDto(ProductReport report) {
        ProductReportDto dto = new ProductReportDto();
        dto.setId(report.getId());
        dto.setProductId(report.getProduct().getId());
        dto.setUserId(report.getUser().getId());
        dto.setReasons(report.getReasons());
        dto.setCustomReason(report.getCustomReason());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus().name());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());

        // Ajouter les détails du produit
        if (report.getProduct() != null) {
            ProductReportDto.ProductDto productDto = new ProductReportDto.ProductDto();
            productDto.setId(report.getProduct().getId());
            productDto.setTitle(report.getProduct().getTitle());
            productDto.setDescription(report.getProduct().getDescription());
            productDto.setPrice(report.getProduct().getPrice());

            // Ajouter les détails du vendeur
            if (report.getProduct().getSeller() != null) {
                ProductReportDto.UserDto sellerDto = new ProductReportDto.UserDto();
                sellerDto.setId(report.getProduct().getSeller().getId());
                sellerDto.setFirstName(report.getProduct().getSeller().getFirstName());
                sellerDto.setLastName(report.getProduct().getSeller().getLastName());
                sellerDto.setEmail(report.getProduct().getSeller().getEmail());
                productDto.setSeller(sellerDto);
            }

            dto.setProduct(productDto);
        }

        // Ajouter les détails du signalant
        if (report.getUser() != null) {
            ProductReportDto.UserDto reporterDto = new ProductReportDto.UserDto();
            reporterDto.setId(report.getUser().getId());
            reporterDto.setFirstName(report.getUser().getFirstName());
            reporterDto.setLastName(report.getUser().getLastName());
            reporterDto.setEmail(report.getUser().getEmail());
            dto.setReporter(reporterDto);
        }

        return dto;
    }
} 