package com.example.db.entity;

import java.util.List;

public class Conversation {
    List<String> conversationId;
    Message message;

    public Conversation(List<String> conversationId, Message message) {
        this.conversationId = conversationId;
        this.message = message;
    }

    public Conversation(List<String> conversationId) {
        this.conversationId = conversationId;
    }

    public List<String> getConversationId() {
        return conversationId;
    }

    public void setConversationId(List<String> conversationId) {
        this.conversationId = conversationId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
