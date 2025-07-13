package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.ConversationDto;
import io.mazy.souqly_backend.dto.CreateConversationRequest;
import io.mazy.souqly_backend.dto.MessageDto;
import io.mazy.souqly_backend.service.ConversationService;
import io.mazy.souqly_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationRestController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    // Récupérer toutes les conversations de l'utilisateur connecté
    @GetMapping
    public ResponseEntity<List<ConversationDto>> getConversations() {
        try {
            Long userId = userService.getCurrentUserId();
            List<ConversationDto> conversations = conversationService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Créer une nouvelle conversation
    @PostMapping
    public ResponseEntity<ConversationDto> createConversation(@RequestBody CreateConversationRequest request) {
        try {
            Long buyerId = userService.getCurrentUserId();
            ConversationDto conversation = conversationService.createConversation(buyerId, request);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupérer les messages d'une conversation
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable String conversationId) {
        try {
            Long userId = userService.getCurrentUserId();
            List<MessageDto> messages = conversationService.getConversationMessages(conversationId, userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Envoyer un message dans une conversation
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable String conversationId,
            @RequestBody MessageRequest request) {
        try {
            Long senderId = userService.getCurrentUserId();
            MessageDto message = conversationService.sendMessage(
                conversationId, 
                request.getText(), 
                senderId, 
                request.getOfferPrice()
            );
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Marquer une conversation comme lue
    @PostMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String conversationId) {
        try {
            Long userId = userService.getCurrentUserId();
            conversationService.markConversationAsRead(conversationId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Classe interne pour les requêtes de message
    public static class MessageRequest {
        private String text;
        private Double offerPrice;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public Double getOfferPrice() { return offerPrice; }
        public void setOfferPrice(Double offerPrice) { this.offerPrice = offerPrice; }
    }
} 