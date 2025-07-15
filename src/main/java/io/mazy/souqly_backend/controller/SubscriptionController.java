package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.SubscriptionRequest;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.service.SubscriptionService;
import io.mazy.souqly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    
    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestBody SubscriptionRequest request) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            subscriptionService.followUser(currentUserId, request.getFollowingId());
            return ResponseEntity.ok("Abonnement créé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/unfollow/{followingId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long followingId) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            subscriptionService.unfollowUser(currentUserId, followingId);
            return ResponseEntity.ok("Désabonnement effectué avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Long userId) {
        try {
            List<UserDto> followers = subscriptionService.getFollowers(userId)
                    .stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable Long userId) {
        try {
            List<UserDto> following = subscriptionService.getFollowing(userId)
                    .stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/is-following/{userId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            boolean isFollowing = subscriptionService.isFollowing(currentUserId, userId);
            return ResponseEntity.ok(isFollowing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 