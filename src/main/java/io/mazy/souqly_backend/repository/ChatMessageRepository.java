package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByConversationIdOrderBySentAtAsc(String conversationId);
} 