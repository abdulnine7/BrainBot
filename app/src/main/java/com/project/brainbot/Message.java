package com.project.brainbot;

public class Message {
    String message;
    User sender;
    long createdAt;

    Message(){
        message = "";
        sender = new User();
        createdAt = 0;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public User getSender() {
        return sender;
    }
}