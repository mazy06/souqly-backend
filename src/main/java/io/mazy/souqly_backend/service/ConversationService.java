package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.ConversationDto;
import io.mazy.souqly_backend.dto.CreateConversationRequest;
import io.mazy.souqly_backend.dto.MessageDto;
import io.mazy.souqly_backend.entity.ChatMessageEntity;
import io.mazy.souqly_backend.entity.ConversationEntity;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.repository.ChatMessageRepository;
import io.mazy.souqly_backend.repository.ConversationRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Récupérer toutes les conversations d'un utilisateur
    public List<ConversationDto> getUserConversations(Long userId) {
        List<ConversationEntity> conversations = conversationRepository.findByUserId(userId);
        
        return conversations.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Créer une nouvelle conversation
    public ConversationDto createConversation(Long buyerId, CreateConversationRequest request) {
        // Vérifier si une conversation existe déjà
        if (conversationRepository.existsByBuyerAndSellerAndProduct(buyerId, request.getSellerId(), request.getProductId())) {
            throw new RuntimeException("Une conversation existe déjà pour ce produit");
        }

        User buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Acheteur non trouvé"));
        
        User seller = userRepository.findById(request.getSellerId())
            .orElseThrow(() -> new RuntimeException("Vendeur non trouvé"));
        
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Créer la conversation
        ConversationEntity conversation = new ConversationEntity();
        conversation.setConversationId(UUID.randomUUID().toString());
        conversation.setBuyer(buyer);
        conversation.setSeller(seller);
        conversation.setProduct(product);
        conversation.setLastMessage(request.getInitialMessage());
        conversation.setLastMessageTime(LocalDateTime.now());
        conversation.setUnreadCountSeller(1); // Le vendeur a un message non lu

        conversation = conversationRepository.save(conversation);

        // Créer le premier message
        ChatMessageEntity firstMessage = new ChatMessageEntity();
        firstMessage.setSender(buyer.getEmail());
        firstMessage.setContent(request.getInitialMessage());
        firstMessage.setConversationId(conversation.getConversationId());
        firstMessage.setSentAt(LocalDateTime.now());
        
        chatMessageRepository.save(firstMessage);

        return convertToDto(conversation);
    }

    // Récupérer les messages d'une conversation
    public List<MessageDto> getConversationMessages(String conversationId, Long currentUserId) {
        ConversationEntity conversation = conversationRepository.findByConversationId(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!conversation.getBuyer().getId().equals(currentUserId) && 
            !conversation.getSeller().getId().equals(currentUserId)) {
            throw new RuntimeException("Accès non autorisé à cette conversation");
        }

        List<ChatMessageEntity> messages = chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
        
        return messages.stream()
            .map(message -> convertToMessageDto(message, currentUserId))
            .collect(Collectors.toList());
    }

    // Envoyer un message dans une conversation
    public MessageDto sendMessage(String conversationId, String content, Long senderId, Double offerPrice) {
        ConversationEntity conversation = conversationRepository.findByConversationId(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créer le message
        ChatMessageEntity message = new ChatMessageEntity();
        message.setSender(sender.getEmail());
        message.setContent(content);
        message.setConversationId(conversationId);
        message.setSentAt(LocalDateTime.now());
        
        message = chatMessageRepository.save(message);

        // Mettre à jour la conversation
        conversation.setLastMessage(content);
        conversation.setLastMessageTime(LocalDateTime.now());
        
        // Incrémenter le compteur de messages non lus pour l'autre utilisateur
        if (conversation.getBuyer().getId().equals(senderId)) {
            conversation.setUnreadCountSeller(conversation.getUnreadCountSeller() + 1);
        } else {
            conversation.setUnreadCountBuyer(conversation.getUnreadCountBuyer() + 1);
        }
        
        conversationRepository.save(conversation);

        return convertToMessageDto(message, senderId);
    }

    // Marquer une conversation comme lue
    public void markConversationAsRead(String conversationId, Long userId) {
        ConversationEntity conversation = conversationRepository.findByConversationId(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        if (conversation.getBuyer().getId().equals(userId)) {
            conversation.setUnreadCountBuyer(0);
        } else if (conversation.getSeller().getId().equals(userId)) {
            conversation.setUnreadCountSeller(0);
        }
        
        conversationRepository.save(conversation);
    }

    // Convertir une entité Conversation en DTO
    private ConversationDto convertToDto(ConversationEntity conversation) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getConversationId());
        dto.setLastMessage(conversation.getLastMessage());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        
        if (conversation.getProduct() != null) {
            dto.setProductId(conversation.getProduct().getId());
            dto.setProductTitle(conversation.getProduct().getTitle());
            // TODO: Ajouter l'URL de l'image du produit
            dto.setProductImageUrl("");
        }
        
        dto.setSellerId(conversation.getSeller().getId());
        
        // Déterminer le nom et l'avatar à afficher
        // TODO: Implémenter la logique pour déterminer quel utilisateur afficher
        dto.setName(conversation.getSeller().getFirstName() + " " + conversation.getSeller().getLastName());
        dto.setAvatarUrl(conversation.getSeller().getProfilePictureUrl());
        
        // Formater le temps
        if (conversation.getLastMessageTime() != null) {
            dto.setTime(conversation.getLastMessageTime().format(TIME_FORMATTER));
        }
        
        // Déterminer le nombre de messages non lus
        // TODO: Implémenter la logique pour déterminer le bon compteur selon l'utilisateur
        dto.setUnreadCount(conversation.getUnreadCountBuyer());
        
        return dto;
    }

    // Convertir une entité ChatMessage en DTO
    private MessageDto convertToMessageDto(ChatMessageEntity message, Long currentUserId) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId().toString());
        dto.setText(message.getContent());
        dto.setSender(message.getSender());
        dto.setConversationId(message.getConversationId());
        dto.setSentAt(message.getSentAt());
        
        // Formater le timestamp
        if (message.getSentAt() != null) {
            dto.setTimestamp(message.getSentAt().format(TIME_FORMATTER));
        }
        
        // TODO: Déterminer si le message est de l'utilisateur actuel
        // Pour l'instant, on utilise une logique simple basée sur l'email
        dto.setIsFromMe(true); // À implémenter correctement
        
        return dto;
    }
} 