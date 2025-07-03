package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.AuthRequest;
import io.mazy.souqly_backend.dto.AuthResponse;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Endpoints pour l'authentification des utilisateurs")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur avec email et mot de passe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "Inscription utilisateur", description = "Crée un nouveau compte utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscription réussie",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur déjà existant")
    })
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        try {
            AuthResponse response = authService.register(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Génère un nouveau token d'accès avec le refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Refresh token invalide")
    })
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        try {
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "Utilisateur courant", description = "Récupère les informations de l'utilisateur connecté")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informations utilisateur récupérées",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            UserDto user = authService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    @PostMapping("/admin/reset-password")
    @Operation(summary = "Reset mot de passe admin (PROVISOIRE)", description = "Change le mot de passe de l'utilisateur admin@souqly.com")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<String> resetAdminPassword(@RequestParam String newPassword) {
        try {
            User updatedUser = authService.resetAdminPassword(newPassword);
            return ResponseEntity.ok("Mot de passe de admin@souqly.com changé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors du changement de mot de passe: " + e.getMessage());
        }
    }
} 