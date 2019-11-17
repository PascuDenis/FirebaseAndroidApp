package com.example.db.entity;

public class ConversationList {
    public String id;

    public ConversationList(String id) {
        this.id = id;
    }

    public ConversationList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
