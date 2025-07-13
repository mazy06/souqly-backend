package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.ChatMessage;
import io.mazy.souqly_backend.entity.ChatMessageEntity;
import io.mazy.souqly_backend.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat/{conversationId}/send")
    @SendTo("/topic/conversations/{conversationId}")
    public ChatMessage sendMessage(@DestinationVariable String conversationId, ChatMessage message) {
        // Sauvegarde en base
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setSender(message.getSender());
        entity.setContent(message.getContent());
        entity.setConversationId(conversationId);
        chatMessageRepository.save(entity);

        return message;
    }
} 