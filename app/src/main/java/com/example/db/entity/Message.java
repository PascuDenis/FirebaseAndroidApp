package com.example.db.entity;

public class Message {
    private String sender;
    private String receiver;
    private String message;
    boolean isSeen;

    public Message(String senderId, String conversationId, String content, boolean isSeen) {
        this.sender = senderId;
        this.receiver = conversationId;
        this.message = content;
        this.isSeen = isSeen;
    }

    public Message() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean idSeen) {
        this.isSeen = idSeen;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", isSeen=" + isSeen +
                '}';
    }
}
