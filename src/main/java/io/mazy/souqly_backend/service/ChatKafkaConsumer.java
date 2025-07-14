package io.mazy.souqly_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mazy.souqly_backend.dto.MessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ChatKafkaConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "chat-messages", groupId = "notification-group")
    public void listen(String messageJson) {
        try {
            MessageDto message = objectMapper.readValue(messageJson, MessageDto.class);
            // TODO: Récupérer le destinataire du message et envoyer une notification push
            // Exemple : notificationService.sendPushNotification(message.getRecipientId(), "Nouveau message de " + message.getSender());
            System.out.println("[Kafka][Notification] Nouveau message reçu pour notification : " + messageJson);
        } catch (Exception e) {
            System.err.println("[Kafka][Notification] Erreur lors du traitement du message : " + e.getMessage());
        }
    }
} 