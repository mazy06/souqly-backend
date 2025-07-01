package io.mazy.souqly_backend.config;

import io.mazy.souqly_backend.entity.Category;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.CategoryRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing data...");
        
        // Create admin user
        createAdminUser();
        
        // Create categories
        createCategories();
        
        log.info("Data initialization completed!");
    }
    
    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@souqly.com")) {
            User admin = new User();
            admin.setEmail("admin@souqly.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("Souqly");
            admin.setRole(User.UserRole.ADMIN);
            admin.setAuthProvider(User.AuthProvider.EMAIL);
            admin.setEnabled(true);
            admin.setGuest(false);
            
            userRepository.save(admin);
            log.info("Admin user created: admin@souqly.com");
        }
    }
    
    private void createCategories() {
        if (categoryRepository.count() == 0) {
            // Root categories
            Category femmes = createCategory("femmes", "Femmes", "human-female", null, null);
            Category hommes = createCategory("hommes", "Hommes", "human-male", null, null);
            Category createurs = createCategory("createurs", "Articles de créateurs", "diamond-stone", null, null);
            Category enfants = createCategory("enfants", "Enfants", "baby-face-outline", null, null);
            Category maison = createCategory("maison", "Maison", "lamp", null, null);
            Category electronique = createCategory("electronique", "Électronique", "power", null, null);
            Category divertissement = createCategory("divertissement", "Divertissement", "book-open-page-variant", null, null);
            Category loisirs = createCategory("loisirs", "Loisirs et collections", "star-outline", "Nouveau", null);
            Category sport = createCategory("sport", "Sport", "tennis-ball", null, null);
            
            // Save root categories
            List<Category> rootCategories = Arrays.asList(femmes, hommes, createurs, enfants, maison, electronique, divertissement, loisirs, sport);
            categoryRepository.saveAll(rootCategories);
            
            // Create subcategories for femmes
            Category vetements = createCategory("vetements", "Vêtements", "human-female", null, femmes);
            Category chaussures = createCategory("chaussures", "Chaussures", "shoe-heel", null, femmes);
            Category sacs = createCategory("sacs", "Sacs", "bag-personal-outline", null, femmes);
            Category accessoires = createCategory("accessoires", "Accessoires", "necklace", null, femmes);
            Category beaute = createCategory("beaute", "Beauté", "bottle-tonic-outline", null, femmes);
            
            List<Category> femmesSubcategories = Arrays.asList(vetements, chaussures, sacs, accessoires, beaute);
            categoryRepository.saveAll(femmesSubcategories);
            
            // Create subcategories for vetements
            Category manteaux = createCategory("manteaux", "Manteaux et vestes", null, null, vetements);
            Category sweats = createCategory("sweats", "Sweats et sweats à capuche", null, null, vetements);
            Category robes = createCategory("robes", "Robes", null, null, vetements);
            Category jupes = createCategory("jupes", "Jupes", null, null, vetements);
            Category hauts = createCategory("hauts", "Hauts et t-shirts", null, null, vetements);
            
            List<Category> vetementsSubcategories = Arrays.asList(manteaux, sweats, robes, jupes, hauts);
            categoryRepository.saveAll(vetementsSubcategories);
            
            log.info("Categories created successfully");
        }
    }
    
    private Category createCategory(String key, String label, String iconName, String badgeText, Category parent) {
        Category category = new Category();
        category.setKey(key);
        category.setLabel(label);
        category.setIconName(iconName);
        category.setBadgeText(badgeText);
        category.setParent(parent);
        category.setSortOrder(0);
        category.setActive(true);
        return category;
    }
} 