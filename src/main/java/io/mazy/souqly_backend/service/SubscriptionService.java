package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.Subscription;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.SubscriptionRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Un utilisateur ne peut pas s'abonner à lui-même");
        }
        
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur abonné non trouvé"));
        
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur à suivre non trouvé"));
        
        if (!subscriptionRepository.existsByFollowerAndFollowing(follower, following)) {
            Subscription subscription = new Subscription();
            subscription.setFollower(follower);
            subscription.setFollowing(following);
            subscriptionRepository.save(subscription);
        }
    }
    
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur abonné non trouvé"));
        
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur à suivre non trouvé"));
        
        subscriptionRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(subscriptionRepository::delete);
    }
    
    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);
        
        if (follower == null || following == null) {
            return false;
        }
        
        return subscriptionRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    public long getFollowersCount(Long userId) {
        return subscriptionRepository.countFollowersByUserId(userId);
    }
    
    public long getFollowingCount(Long userId) {
        return subscriptionRepository.countFollowingByUserId(userId);
    }
    
    public List<User> getFollowers(Long userId) {
        return subscriptionRepository.findFollowersByFollowingId(userId);
    }
    
    public List<User> getFollowing(Long userId) {
        return subscriptionRepository.findFollowingByFollowerId(userId);
    }
} 