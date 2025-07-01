package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.ProductCreateRequest;
import io.mazy.souqly_backend.entity.Category;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.ProductImage;
import io.mazy.souqly_backend.entity.User;
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

import java.util.List;
import java.util.Optional;

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

    public Page<Product> getProducts(Pageable pageable, Long categoryId, Double minPrice, 
                                   Double maxPrice, String condition, String brand, 
                                   String size, String search, String sortBy, String sortOrder) {
        
        // Créer le tri
        Sort sort = Sort.unsorted();
        if (sortBy != null && sortOrder != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            
            switch (sortBy.toLowerCase()) {
                case "price":
                    sort = Sort.by(direction, "price");
                    break;
                case "createdat":
                    sort = Sort.by(direction, "createdAt");
                    break;
                case "favoritecount":
                    sort = Sort.by(direction, "favoriteCount");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        // Créer la pageable avec le tri
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        
        // Utiliser la méthode avec filtres
        return productRepository.findProductsWithFilters(
            Product.ProductStatus.ACTIVE, 
            categoryId, 
            minPrice, 
            maxPrice, 
            condition, 
            brand, 
            size, 
            search, 
            pageableWithSort
        );
    }

    public Page<Product> getProductsForListing(Pageable pageable, Long categoryId, Double minPrice, 
                                             Double maxPrice, String condition, String brand, 
                                             String size, String search, String sortBy, String sortOrder) {
        // Temporairement, récupérer tous les produits sans filtre de statut
        return productRepository.findAllProducts(pageable);
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findByIdWithImages(id);
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<Product> getFavoriteProducts(Long userId) {
        // TODO: Implémenter la logique des favoris
        return productRepository.findBySellerId(userId); // Temporaire
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
        
        // Basculer entre ACTIVE et INACTIVE
        if (product.getStatus() == Product.ProductStatus.ACTIVE) {
            product.setStatus(Product.ProductStatus.INACTIVE);
        } else {
            product.setStatus(Product.ProductStatus.ACTIVE);
        }
        
        return productRepository.save(product);
    }
} 