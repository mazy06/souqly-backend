package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.AddressDto;
import io.mazy.souqly_backend.dto.PasswordUpdateDto;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.dto.UserProfileDto;
import io.mazy.souqly_backend.dto.UserProfileUpdateDto;
import io.mazy.souqly_backend.dto.UserProfileDetailDto;
import io.mazy.souqly_backend.entity.Address;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.UserRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final SubscriptionService subscriptionService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // Profile management methods
    public UserProfileDto getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return mapToUserProfileDto(user);
    }

    public UserProfileDto updateUserProfile(String email, UserProfileUpdateDto updateDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Update basic fields
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        
        // Update address
        if (updateDto.getAddress() != null) {
            AddressDto addressDto = updateDto.getAddress();
            Address address = user.getAddress();
            if (address == null) {
                address = new Address();
                user.setAddress(address);
            }
            
            if (addressDto.getStreet() != null) {
                address.setStreet(addressDto.getStreet());
            }
            if (addressDto.getCity() != null) {
                address.setCity(addressDto.getCity());
            }
            if (addressDto.getState() != null) {
                address.setState(addressDto.getState());
            }
            if (addressDto.getZipCode() != null) {
                address.setZipCode(addressDto.getZipCode());
            }
            if (addressDto.getCountry() != null) {
                address.setCountry(addressDto.getCountry());
            }
        }
        
        User savedUser = userRepository.save(user);
        return mapToUserProfileDto(savedUser);
    }

    public void updatePassword(String email, PasswordUpdateDto passwordDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Verify current password
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }

    public String updateProfilePicture(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        try {
            // Create uploads directory if it doesn't exist
            String uploadDir = "uploads/profile-pictures";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // Update user profile picture URL
            String profilePictureUrl = "/api/users/profile-picture/" + filename;
            user.setProfilePictureUrl(profilePictureUrl);
            userRepository.save(user);
            
            return profilePictureUrl;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }

    // Additional methods required by other services
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDto::new);
    }

    public User changePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Page<UserDto> getAllUsers(Pageable pageable, String search, String status, String role) {
        Page<User> usersPage = userRepository.findAll(pageable);
        
        return usersPage.map(user -> {
            int adsCount = productRepository.findBySellerId(user.getId()).size();
            return new UserDto(user, adsCount);
        });
    }

    public Optional<UserDto> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            int adsCount = productRepository.findBySellerId(id).size();
            return Optional.of(new UserDto(userOpt.get(), adsCount));
        }
        return Optional.empty();
    }

    public UserDto updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setProfilePictureUrl(userDetails.getProfilePictureUrl());
        
        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
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
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public boolean isAdmin(Long id) {
        return userRepository.findById(id)
                .map(user -> user.getRole() == User.UserRole.ADMIN)
                .orElse(false);
    }

    public boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == User.UserRole.ADMIN)
                .orElse(false);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Mapping methods
    private UserProfileDto mapToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        
        if (user.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet(user.getAddress().getStreet());
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setState(user.getAddress().getState());
            addressDto.setZipCode(user.getAddress().getZipCode());
            addressDto.setCountry(user.getAddress().getCountry());
            dto.setAddress(addressDto);
        }
        
        return dto;
    }

    private UserDto mapToUserDto(User user) {
        return new UserDto(user);
    }

    // Récupérer l'utilisateur connecté
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            final String username;
            
            // Gérer différents types de Principal
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User userDetails = 
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                username = userDetails.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
            } else {
                throw new RuntimeException("Type de Principal non supporté");
            }
            
            return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + username));
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    // Récupérer l'ID de l'utilisateur connecté
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    // Mettre à jour lastLoginAt
    public void updateLastLoginAt(User user) {
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }
    
    // Méthodes pour le profil détaillé
    public UserProfileDetailDto getUserProfileDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        Long currentUserId = getCurrentUserId();
        boolean isOwnProfile = currentUserId.equals(userId);
        
        long followersCount = subscriptionService.getFollowersCount(userId);
        long followingCount = subscriptionService.getFollowingCount(userId);
        long productsCount = productRepository.findBySellerId(userId).size();
        boolean isFollowing = !isOwnProfile && subscriptionService.isFollowing(currentUserId, userId);
        
        return UserProfileDetailDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .profilePictureUrl(user.getProfilePictureUrl())
                .address(mapToAddressDto(user.getAddress()))
                .createdAt(user.getCreatedAt())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .productsCount(productsCount)
                .isFollowing(isFollowing)
                .isOwnProfile(isOwnProfile)
                .build();
    }
    
    private AddressDto mapToAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(address.getStreet());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setZipCode(address.getZipCode());
        addressDto.setCountry(address.getCountry());
        return addressDto;
    }
} 