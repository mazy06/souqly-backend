package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.dto.UserActionRequest;
import io.mazy.souqly_backend.dto.UserStatsResponse;
import io.mazy.souqly_backend.dto.CreateModeratorRequest;
import io.mazy.souqly_backend.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;



    /**
     * Récupérer les statistiques des utilisateurs
     */
    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        UserStatsResponse stats = userManagementService.getUserStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Créer un modérateur (admin seulement)
     */
    @PostMapping("/moderators")
    public ResponseEntity<UserDto> createModerator(@RequestBody CreateModeratorRequest request) {
        UserDto createdModerator = userManagementService.createModerator(request.getEmail());
        return ResponseEntity.ok(createdModerator);
    }

    /**
     * Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userManagementService.getUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Effectuer une action sur un utilisateur
     */
    @PostMapping("/{id}/action")
    public ResponseEntity<UserDto> performUserAction(
            @PathVariable Long id,
            @RequestBody UserActionRequest request) {
        
        UserDto updatedUser = userManagementService.performUserAction(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Mettre à jour le statut d'un utilisateur
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String status = request.get("status");
        UserDto updatedUser = userManagementService.updateUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Mettre à jour le rôle d'un utilisateur
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String role = request.get("role");
        UserDto updatedUser = userManagementService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 