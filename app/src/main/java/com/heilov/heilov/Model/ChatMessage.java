package com.heilov.heilov.Model;

import java.util.Date;



public class ChatMessage {

    private String messageText;
    private User messageUser;
    private long messageTime;

    public ChatMessage(String messageText, User messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public User getMessageUser() {
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public ChatMessage() {
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageText='" + messageText + '\'' +
                ", messageUser='" + messageUser + '\'' +
                ", messageTime=" + messageTime +
                '}';
    }
}
