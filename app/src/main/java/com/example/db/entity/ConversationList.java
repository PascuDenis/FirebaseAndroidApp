package com.example.db.entity;

public class ConversationList {
    private String id;


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

    @Override
    public String toString() {
        return "ConversationList{" +
                "id='" + id + '\'' +
                '}';
    }
}
