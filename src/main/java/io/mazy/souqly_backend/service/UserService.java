package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.AddressDto;
import io.mazy.souqly_backend.dto.PasswordUpdateDto;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.dto.UserProfileDto;
import io.mazy.souqly_backend.dto.UserProfileUpdateDto;
import io.mazy.souqly_backend.entity.Address;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToUserDto);
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
} 