package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.elasticsearch.ProductDocument;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.elasticsearch.ProductSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElasticsearchService {

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Synchronise tous les produits de la base de données vers Elasticsearch
     */
    @Transactional
    public void syncAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDocument> documents = products.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());
        
        productSearchRepository.saveAll(documents);
    }

    /**
     * Synchronise un produit spécifique
     */
    @Transactional
    public void syncProduct(Product product) {
        ProductDocument document = convertToDocument(product);
        productSearchRepository.save(document);
    }

    /**
     * Supprime un produit d'Elasticsearch
     */
    @Transactional
    public void deleteProduct(String productId) {
        productSearchRepository.deleteById(productId);
    }

    /**
     * Recherche de produits avec Elasticsearch
     */
    public Page<ProductDocument> searchProducts(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return productSearchRepository.findAll(pageable);
        }
        return productSearchRepository.searchProducts(query.trim(), pageable);
    }

    /**
     * Recherche avec filtres
     */
    public Page<ProductDocument> searchProductsWithFilters(String query, Double minPrice, Double maxPrice,
                                                        Long categoryId, String condition, String brand,
                                                        String size, String city, String country, Pageable pageable) {
        return productSearchRepository.searchProductsWithFilters(query, minPrice, maxPrice, categoryId,
                condition, brand, size, city, country, pageable);
    }

    /**
     * Recherche par catégorie
     */
    public Page<ProductDocument> findByCategoryId(Long categoryId, Pageable pageable) {
        return productSearchRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Recherche par vendeur
     */
    public Page<ProductDocument> findBySellerId(Long sellerId, Pageable pageable) {
        return productSearchRepository.findBySellerId(sellerId, pageable);
    }

    /**
     * Recherche par ville
     */
    public Page<ProductDocument> findByCity(String city, Pageable pageable) {
        return productSearchRepository.findByCity(city, pageable);
    }

    /**
     * Recherche par marque
     */
    public Page<ProductDocument> findByBrand(String brand, Pageable pageable) {
        return productSearchRepository.findByBrand(brand, pageable);
    }

    /**
     * Recherche par condition
     */
    public Page<ProductDocument> findByCondition(String condition, Pageable pageable) {
        return productSearchRepository.findByCondition(condition, pageable);
    }

    /**
     * Recherche par gamme de prix
     */
    public Page<ProductDocument> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return productSearchRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    /**
     * Recherche de suggestions (autocomplete)
     */
    public List<ProductDocument> findSuggestions(String query, Pageable pageable) {
        return productSearchRepository.findSuggestions(query, pageable);
    }

    /**
     * Recherche de produits populaires
     */
    public Page<ProductDocument> findPopularProducts(Pageable pageable) {
        return productSearchRepository.findPopularProducts(pageable);
    }

    /**
     * Recherche de produits récents
     */
    public Page<ProductDocument> findRecentProducts(Pageable pageable) {
        return productSearchRepository.findRecentProducts(pageable);
    }

    /**
     * Recherche par tags
     */
    public Page<ProductDocument> findByTags(String tag, Pageable pageable) {
        return productSearchRepository.findByTags(tag, pageable);
    }

    /**
     * Convertit une entité Product en ProductDocument
     */
    private ProductDocument convertToDocument(Product product) {
        String sellerName = null;
        if (product.getSeller() != null) {
            String firstName = product.getSeller().getFirstName();
            String lastName = product.getSeller().getLastName();
            if (firstName != null && lastName != null) {
                sellerName = firstName + " " + lastName;
            } else if (firstName != null) {
                sellerName = firstName;
            } else if (lastName != null) {
                sellerName = lastName;
            } else {
                sellerName = product.getSeller().getEmail();
            }
        }

        return new ProductDocument(
                product.getId().toString(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getBrand(),
                product.getSize(),
                product.getCondition(),
                product.getShippingInfo(),
                product.getCity(),
                product.getCountry(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getLabel() : null,
                product.getSeller() != null ? product.getSeller().getId() : null,
                sellerName,
                product.getStatus() != null ? product.getStatus().name() : null,
                product.getIsActive(),
                product.getViewCount(),
                product.getFavoriteCount(),
                product.getCreatedAt() != null ? product.getCreatedAt().toString() : null,
                product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null,
                product.getTags()
        );
    }

    /**
     * Récupère les produits depuis Elasticsearch et les convertit en entités Product
     */
    public List<Product> getProductsFromDocuments(List<ProductDocument> documents) {
        List<Long> productIds = documents.stream()
                .map(doc -> Long.parseLong(doc.getId()))
                .collect(Collectors.toList());
        
        return productRepository.findAllById(productIds);
    }
} 