package com.heilov.heilov.Model;

import java.util.ArrayList;

public class Conversation {
    private ArrayList<ChatMessage> listMessageData;

    public Conversation() {
        listMessageData = new ArrayList<>();
    }

    public ArrayList<ChatMessage> getListMessageData() {
        return listMessageData;
    }
}
