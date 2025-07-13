package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.ChatMessage;
import io.mazy.souqly_backend.entity.ChatMessageEntity;
import io.mazy.souqly_backend.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/history/{conversationId}")
    public List<ChatMessage> getHistory(@PathVariable String conversationId) {
        return chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
            .stream()
            .map(entity -> new ChatMessage(entity.getSender(), entity.getContent(), entity.getConversationId()))
            .collect(Collectors.toList());
    }
} 