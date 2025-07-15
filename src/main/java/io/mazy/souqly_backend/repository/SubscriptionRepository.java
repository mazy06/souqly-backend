package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.Subscription;
import io.mazy.souqly_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);
    
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    @Query("SELECT s.following FROM Subscription s WHERE s.follower.id = :followerId")
    List<User> findFollowingByFollowerId(@Param("followerId") Long followerId);
    
    @Query("SELECT s.follower FROM Subscription s WHERE s.following.id = :followingId")
    List<User> findFollowersByFollowingId(@Param("followingId") Long followingId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.following.id = :userId")
    long countFollowersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.follower.id = :userId")
    long countFollowingByUserId(@Param("userId") Long userId);
} 