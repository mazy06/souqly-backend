package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::new);
    }
    
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDto::new);
    }
    
    public UserDto createUser(User user) {
        // Encode password if not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }
    
    public Optional<UserDto> updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(userDetails.getFirstName());
                    user.setLastName(userDetails.getLastName());
                    user.setProfilePicture(userDetails.getProfilePicture());
                    user.setEnabled(userDetails.isEnabled());
                    user.setRole(userDetails.getRole());
                    
                    // Only update password if provided and not already encoded
                    if (userDetails.getPassword() != null && !userDetails.getPassword().startsWith("$2a$")) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    
                    User savedUser = userRepository.save(user);
                    return new UserDto(savedUser);
                });
    }
    
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<UserDto> getAllAdmins() {
        return userRepository.findAllAdmins().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
    
    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == User.UserRole.ADMIN)
                .orElse(false);
    }
    
    public boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == User.UserRole.ADMIN)
                .orElse(false);
    }
    
    public Optional<User> findByProviderIdAndAuthProvider(String providerId, User.AuthProvider authProvider) {
        return userRepository.findByProviderIdAndAuthProvider(providerId, authProvider);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
} 