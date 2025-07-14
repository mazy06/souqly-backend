package io.mazy.souqly_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatKafkaProducer {
    private static final String TOPIC = "chat-messages";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String messageJson) {
        kafkaTemplate.send(TOPIC, messageJson);
    }

    // TODO: Ajouter un topic et un producer pour l'analytics (ex: topic 'chat-analytics')
    // TODO: Ajouter un topic et un producer pour la modération automatique (ex: topic 'chat-moderation')
    // TODO: Ajouter un topic et un producer pour l'envoi d'emails (ex: topic 'chat-emails')
    // TODO: Ajouter un topic et un producer pour les notifications promotionnelles (ex: topic 'promotions')
} 