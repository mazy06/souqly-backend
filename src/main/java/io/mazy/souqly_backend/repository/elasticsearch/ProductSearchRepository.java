package io.mazy.souqly_backend.repository.elasticsearch;

import io.mazy.souqly_backend.entity.elasticsearch.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    // Recherche simple par titre et description
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"description\", \"brand\", \"categoryName\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> searchProducts(String query, Pageable pageable);

    // Recherche avec filtres
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"description\", \"brand\", \"categoryName\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}], \"filter\": [{\"term\": {\"isActive\": true}}, {\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}, {\"term\": {\"categoryId\": ?3}}, {\"term\": {\"condition\": ?4}}, {\"term\": {\"brand\": ?5}}, {\"term\": {\"size\": ?6}}, {\"term\": {\"city\": ?7}}, {\"term\": {\"country\": ?8}}]}}")
    Page<ProductDocument> searchProductsWithFilters(String query, Double minPrice, Double maxPrice, 
                                                  Long categoryId, String condition, String brand, 
                                                  String size, String city, String country, Pageable pageable);

    // Recherche par catégorie
    @Query("{\"bool\": {\"must\": [{\"term\": {\"categoryId\": ?0}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findByCategoryId(Long categoryId, Pageable pageable);

    // Recherche par vendeur
    @Query("{\"bool\": {\"must\": [{\"term\": {\"sellerId\": ?0}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findBySellerId(Long sellerId, Pageable pageable);

    // Recherche par ville
    @Query("{\"bool\": {\"must\": [{\"term\": {\"city\": ?0}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findByCity(String city, Pageable pageable);

    // Recherche par marque
    @Query("{\"bool\": {\"must\": [{\"term\": {\"brand\": ?0}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findByBrand(String brand, Pageable pageable);

    // Recherche par condition
    @Query("{\"bool\": {\"must\": [{\"term\": {\"condition\": ?0}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findByCondition(String condition, Pageable pageable);

    // Recherche par gamme de prix
    @Query("{\"bool\": {\"must\": [{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);

    // Recherche de suggestions (autocomplete)
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\", \"brand^2\", \"categoryName\"], \"type\": \"phrase_prefix\"}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    List<ProductDocument> findSuggestions(String query, Pageable pageable);

    // Recherche de produits populaires (par nombre de vues et favoris)
    @Query("{\"bool\": {\"filter\": [{\"term\": {\"isActive\": true}}]}, \"sort\": [{\"viewCount\": {\"order\": \"desc\"}}, {\"favoriteCount\": {\"order\": \"desc\"}}]}")
    Page<ProductDocument> findPopularProducts(Pageable pageable);

    // Recherche de produits récents
    @Query("{\"bool\": {\"filter\": [{\"term\": {\"isActive\": true}}]}, \"sort\": [{\"createdAt\": {\"order\": \"desc\"}}]}")
    Page<ProductDocument> findRecentProducts(Pageable pageable);

    // Recherche de produits par tags
    @Query("{\"bool\": {\"must\": [{\"nested\": {\"path\": \"tags\", \"query\": {\"term\": {\"tags\": ?0}}}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<ProductDocument> findByTags(String tag, Pageable pageable);
} 