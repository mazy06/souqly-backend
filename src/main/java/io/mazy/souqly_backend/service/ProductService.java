package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.ProductCreateRequest;
import io.mazy.souqly_backend.entity.Category;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.ProductImage;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.entity.ProductView;
import io.mazy.souqly_backend.repository.CategoryRepository;
import io.mazy.souqly_backend.repository.ProductImageRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import io.mazy.souqly_backend.dto.ProductListDTO;
import io.mazy.souqly_backend.entity.elasticsearch.ProductDocument;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;
import io.mazy.souqly_backend.repository.ProductViewRepository;
import io.mazy.souqly_backend.dto.ProductMyProductsDTO;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private ElasticsearchService elasticsearchService;
    
    @Autowired
    private ProductViewRepository productViewRepository;

    public List<Product> findProductsByImage(MultipartFile image) {
        // TODO: Logique de similarité d'image
        // Pour le POC, retourne tous les produits actifs
        return productRepository.findByIsActiveTrue();
    }

    @Transactional
    public Product createProduct(ProductCreateRequest req, Long sellerId) {
        Category category = categoryRepository.findById(req.categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("Vendeur non trouvé"));

        Product product = new Product();
        product.setTitle(req.title);
        product.setDescription(req.description);
        product.setPrice(req.price);
        product.setBrand(req.brand);
        product.setSize(req.size);
        product.setCondition(req.condition);
        product.setShippingInfo(req.shippingInfo);
        product.setCity(req.getCity());
        product.setCountry(req.getCountry());
        product.setCategory(category);
        product.setSeller(seller);
        product.setStatus(Product.ProductStatus.ACTIVE);

        product = productRepository.save(product);

        // Associer les images au produit
        if (req.imageIds != null && !req.imageIds.isEmpty()) {
            List<ProductImage> images = productImageRepository.findAllById(req.imageIds);
            for (ProductImage image : images) {
                image.setProduct(product);
                productImageRepository.save(image);
            }
        }

        return product;
    }



    public Page<Product> getProductsForListing(Pageable pageable, Long categoryId, Double minPrice, 
                                             Double maxPrice, String condition, String brand, 
                                             String size, String search, String sortBy, String sortOrder) {
        System.out.println("[ProductService] Recherche search = '" + search + "'");
        
        // Utiliser Elasticsearch pour la recherche
        try {
            if (search != null && !search.trim().isEmpty()) {
                System.out.println("[ProductService] Utilisation d'Elasticsearch pour la recherche !");
                // Recherche avec Elasticsearch
                Page<ProductDocument> elasticResults = elasticsearchService.searchProducts(search.trim(), pageable);
                
                // Convertir les ProductDocument en Product
                List<Long> productIds = elasticResults.getContent().stream()
                    .map(doc -> Long.parseLong(doc.getId()))
                    .collect(Collectors.toList());
                
                if (!productIds.isEmpty()) {
                    List<Product> products = productRepository.findAllById(productIds);
                    
                    // Créer une Page<Product> avec les résultats d'Elasticsearch
                    return new PageImpl<>(products, pageable, elasticResults.getTotalElements());
                } else {
                    return new PageImpl<>(new ArrayList<>(), pageable, 0);
                }
            } else {
                System.out.println("[ProductService] Recherche sans filtre textuel - utilisation de la base de données.");
                return productRepository.findActiveAndSoldProducts(pageable);
            }
        } catch (Exception e) {
            System.out.println("[ProductService] Erreur Elasticsearch, fallback vers la recherche basique: " + e.getMessage());
            // Fallback vers la recherche basique
            if (search != null && !search.trim().isEmpty()) {
                return productRepository.findByIsActiveAndTitleContainingIgnoreCase(true, search.trim(), pageable);
            } else {
                return productRepository.findActiveAndSoldProducts(pageable);
            }
        }
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findByIdWithImages(id);
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        List<Product> products = productRepository.findBySellerId(sellerId);
        
        // Charger explicitement les relations lazy pour éviter les erreurs de sérialisation
        for (Product product : products) {
            // Forcer le chargement de la catégorie
            if (product.getCategory() != null) {
                product.getCategory().getId();
                // Charger les sous-catégories si elles existent
                if (product.getCategory().getChildren() != null) {
                    product.getCategory().getChildren().size();
                }
            }
            // Forcer le chargement du vendeur
            if (product.getSeller() != null) {
                product.getSeller().getId();
            }
            // Charger les images
            product.setImages(productImageRepository.findByProductId(product.getId()));
        }
        
        return products;
    }

    public List<Product> getProductsBySellerAndStatus(Long sellerId, String status) {
        System.out.println("[ProductService] getProductsBySellerAndStatus appelé - sellerId: " + sellerId + ", status: " + status);
        
        List<Product> products;
        
        if (status == null || status.isEmpty()) {
            System.out.println("[ProductService] Aucun statut spécifié, retour de tous les produits");
            products = productRepository.findBySellerId(sellerId);
        } else {
            try {
                // Gérer le cas spécial TERMINATED
                if ("TERMINATED".equals(status.toUpperCase())) {
                    System.out.println("[ProductService] Récupération des produits terminés (INACTIVE, SOLD, DELETED)");
                    products = productRepository.findBySellerIdAndTerminatedStatus(sellerId);
                } else {
                    Product.ProductStatus productStatus = Product.ProductStatus.valueOf(status.toUpperCase());
                    System.out.println("[ProductService] Statut valide: " + productStatus);
                    products = productRepository.findBySellerIdAndStatus(sellerId, productStatus);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[ProductService] Statut invalide: " + status + ", retour de tous les produits");
                // Si le statut n'est pas valide, retourner tous les produits du vendeur
                products = productRepository.findBySellerId(sellerId);
            }
        }
        
        System.out.println("[ProductService] Produits trouvés: " + products.size());
        
        // Charger explicitement les relations lazy pour éviter les erreurs de sérialisation
        for (Product product : products) {
            // Forcer le chargement de la catégorie
            if (product.getCategory() != null) {
                product.getCategory().getId();
                // Charger les sous-catégories si elles existent
                if (product.getCategory().getChildren() != null) {
                    product.getCategory().getChildren().size();
                }
            }
            // Forcer le chargement du vendeur
            if (product.getSeller() != null) {
                product.getSeller().getId();
            }
            // Charger les images
            product.setImages(productImageRepository.findByProductId(product.getId()));
        }
        
        return products;
    }

    private void initializeCategoryChildren(io.mazy.souqly_backend.entity.Category category) {
        if (category != null && category.getChildren() != null) {
            // Force le chargement de la liste des enfants
            category.getChildren().size();
            
            // Force le chargement de chaque enfant et de ses propres enfants
            for (io.mazy.souqly_backend.entity.Category child : category.getChildren()) {
                // Force le chargement de tous les champs de l'enfant
                child.getId();
                child.getKey();
                child.getLabel();
                child.getIconName();
                child.getBadgeText();
                child.isActive();
                child.getSortOrder();
                
                // Force le chargement récursif des enfants de cet enfant
                initializeCategoryChildren(child);
            }
        }
    }

    public List<ProductMyProductsDTO> getProductsBySellerAndStatusDTO(Long sellerId, String status) {
        System.out.println("[ProductService] getProductsBySellerAndStatusDTO ENTRÉE - sellerId: " + sellerId + ", status: " + status);
        List<Product> products = getProductsBySellerAndStatus(sellerId, status);
        List<ProductMyProductsDTO> dtos = new ArrayList<>();
        System.out.println("[ProductService] getProductsBySellerAndStatusDTO - Nombre de produits: " + products.size());
        
        for (Product product : products) {
            System.out.println("[ProductService] Traitement du produit ID: " + product.getId() + ", Title: " + product.getTitle());
            if (product.getCategory() != null) {
                initializeCategoryChildren(product.getCategory());
            }
            ProductMyProductsDTO dto = new ProductMyProductsDTO(product);
            System.out.println("[ProductService] DTO créé - ID: " + dto.getId() + ", Title: " + dto.getTitle());
            dtos.add(dto);
        }
        
        System.out.println("[ProductService] Nombre de DTOs créés: " + dtos.size());
        return dtos;
    }

    public List<ProductMyProductsDTO> getProductsBySellerDTO(Long sellerId) {
        List<Product> products = getProductsBySeller(sellerId);
        List<ProductMyProductsDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory() != null) {
                initializeCategoryChildren(product.getCategory());
            }
            dtos.add(new ProductMyProductsDTO(product));
        }
        return dtos;
    }

    public List<Product> getFavoriteProducts(Long userId) {
        // TODO: Implémenter la logique des favoris
        // Pour l'instant, retourner une liste vide
        return new ArrayList<>();
    }

    /**
     * Vide le cache et force le rechargement des données
     */
    public void clearCache() {
        // Éviter le cache Hibernate pour forcer le rechargement depuis la base
        productRepository.flush();
        System.out.println("[ProductService] Cache vidé - données rechargées depuis la base");
    }

    @Transactional
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce produit");
        }
        
        productRepository.delete(product);
    }

    @Transactional
    public Product updateProduct(Long productId, ProductCreateRequest req, Long sellerId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        if (req.title != null) product.setTitle(req.title);
        if (req.description != null) product.setDescription(req.description);
        if (req.price != null) product.setPrice(req.price);
        if (req.brand != null) product.setBrand(req.brand);
        if (req.size != null) product.setSize(req.size);
        if (req.condition != null) product.setCondition(req.condition);
        if (req.shippingInfo != null) product.setShippingInfo(req.shippingInfo);
        if (req.getCity() != null) product.setCity(req.getCity());
        if (req.getCountry() != null) product.setCountry(req.getCountry());
        
        if (req.categoryId != null) {
            Category category = categoryRepository.findById(req.categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    public void toggleFavorite(Long productId, Long userId) {
        // TODO: Implémenter la logique des favoris
        // Pour l'instant, on incrémente juste le compteur
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        product.setFavoriteCount(product.getFavoriteCount() + 1);
        productRepository.save(product);
    }

    @Transactional
    public Product toggleProductStatus(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce produit");
        }
        
        // Basculer entre actif et inactif en utilisant le champ isActive
        if (product.getIsActive()) {
            product.setIsActive(false);
            product.setStatus(Product.ProductStatus.INACTIVE); // Pour compatibilité
        } else {
            product.setIsActive(true);
            product.setStatus(Product.ProductStatus.ACTIVE); // Pour compatibilité
        }
        
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProductStatus(Long productId, String status, Long sellerId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce produit");
        }
        
        try {
            Product.ProductStatus productStatus = Product.ProductStatus.valueOf(status);
            
            if (Product.ProductStatus.ACTIVE.equals(productStatus)) {
                product.setIsActive(true);
                product.setStatus(Product.ProductStatus.ACTIVE);
            } else if (Product.ProductStatus.INACTIVE.equals(productStatus)) {
                product.setIsActive(false);
                product.setStatus(Product.ProductStatus.INACTIVE);
            } else {
                throw new IllegalArgumentException("Statut invalide: " + status);
            }
            
            return productRepository.save(product);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut invalide: " + status);
        }
    }

    public Page<ProductListDTO> getProductsForListingCacheable(Pageable pageable) {
        Page<Product> productPage = productRepository.findActiveAndSoldProducts(pageable);
        return productPage.map(product -> new ProductListDTO(product, product.getFavoriteCount()));
    }

    @Transactional
    public void incrementViewCount(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        // Vérifier si l'utilisateur a déjà vu ce produit
        if (!productViewRepository.existsByProductIdAndUserId(productId, userId)) {
            // Créer une nouvelle vue
            ProductView productView = new ProductView(product, userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé")));
            productViewRepository.save(productView);
            
            // Incrémenter le compteur de vues du produit
            product.setViewCount(product.getViewCount() + 1);
            productRepository.save(product);
        }
    }

    @Transactional
    public Product markAsSold(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce produit");
        }
        
        // Marquer le produit comme vendu
        product.setStatus(Product.ProductStatus.SOLD);
        product.setIsActive(false); // Désactiver le produit
        
        System.out.println("[ProductService] Produit " + productId + " marqué comme vendu par l'utilisateur " + sellerId);
        
        return productRepository.save(product);
    }
} 