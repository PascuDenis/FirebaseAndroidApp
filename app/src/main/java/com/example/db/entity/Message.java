package com.example.db.entity;

import java.util.Date;

public class Message {
    private String id;
    private String conversationId;
    private String content;
    private Date timeCeated;

    public Message(String id, String conversationId, String content, Date timeCeated) {
        this.id = id;
        this.conversationId = conversationId;
        this.content = content;
        this.timeCeated = timeCeated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimeCeated() {
        return timeCeated;
    }

    public void setTimeCeated(Date timeCeated) {
        this.timeCeated = timeCeated;
    }
}
