package io.mazy.souqly_backend.dto;

public class ChatMessage {
    private String sender;
    private String content;
    private String conversationId;

    public ChatMessage() {}

    public ChatMessage(String sender, String content, String conversationId) {
        this.sender = sender;
        this.content = content;
        this.conversationId = conversationId;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
} 