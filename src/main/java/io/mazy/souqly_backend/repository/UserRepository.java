package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderIdAndAuthProvider(String providerId, User.AuthProvider authProvider);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();
    
    @Query("SELECT u FROM User u WHERE u.guest = true")
    List<User> findAllGuests();
    
    @Query("SELECT u FROM User u WHERE u.enabled = :enabled")
    List<User> findByEnabled(@Param("enabled") boolean enabled);
    
    @Query("SELECT u FROM User u WHERE u.authProvider = :provider")
    List<User> findByAuthProvider(@Param("provider") User.AuthProvider provider);
    
    // Méthodes pour la gestion des utilisateurs
    long countByEnabledTrue();
    
    long countByEnabledFalse();
    
    long countByBannedTrue();
    
    long countByRole(User.UserRole role);
    

} 