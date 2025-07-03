package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.AuthRequest;
import io.mazy.souqly_backend.dto.AuthResponse;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(jwtToken, refreshToken, user, 86400000L);
    }
    
    public AuthResponse register(User user) {
        // Check if user already exists
        if (userService.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        
        // Create new user
        UserDto createdUser = userService.createUser(user);
        User savedUser = userService.findByEmail(user.getEmail()).orElseThrow();
        
        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return new AuthResponse(jwtToken, refreshToken, savedUser, 86400000L);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        
        if (userEmail != null) {
            User user = userService.findByEmail(userEmail).orElseThrow();
            
            if (jwtService.isTokenValid(refreshToken, user)) {
                String newJwtToken = jwtService.generateToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);
                
                return new AuthResponse(newJwtToken, newRefreshToken, user, 86400000L);
            }
        }
        
        throw new RuntimeException("Invalid refresh token");
    }
    
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userService.getUserByEmail(userEmail).orElseThrow();
    }
    
    /**
     * Reset le mot de passe de l'utilisateur admin (endpoint provisoire)
     */
    public boolean resetAdminPassword(String newPassword) {
        return userService.changePasswordByEmail("admin@souqly.com", newPassword);
    }
} 