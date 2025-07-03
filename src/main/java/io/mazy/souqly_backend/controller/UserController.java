package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.dto.UserProfileDto;
import io.mazy.souqly_backend.dto.UserProfileUpdateDto;
import io.mazy.souqly_backend.dto.PasswordUpdateDto;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // ===== ENDPOINTS DE PROFIL (AVANT LES ENDPOINTS AVEC {id}) =====
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDto profile = userService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UserProfileUpdateDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserProfileDto updatedProfile = userService.updateUserProfile(userDetails.getUsername(), updateDto);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestBody PasswordUpdateDto passwordDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        userService.updatePassword(userDetails.getUsername(), passwordDto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String imageUrl = userService.updateProfilePicture(userDetails.getUsername(), file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
    
    // ===== ENDPOINTS AVEC PARAMÈTRES DYNAMIQUES (APRÈS LES ENDPOINTS FIXES) =====
    
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        UserDto createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
    
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        UserDto updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllAdmins() {
        List<UserDto> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/{id:[0-9]+}/is-admin")
    public ResponseEntity<Boolean> isAdmin(@PathVariable Long id) {
        boolean isAdmin = userService.isAdmin(id);
        return ResponseEntity.ok(isAdmin);
    }
    
    @GetMapping("/email/{email}/is-admin")
    public ResponseEntity<Boolean> isAdminByEmail(@PathVariable String email) {
        boolean isAdmin = userService.isAdmin(email);
        return ResponseEntity.ok(isAdmin);
    }
    
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
