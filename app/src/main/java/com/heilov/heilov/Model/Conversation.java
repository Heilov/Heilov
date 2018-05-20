package com.heilov.heilov.Model;

import java.util.ArrayList;

public class Conversation {
    private ArrayList<ChatMessage> listMessageData;
    private String uid;
    private User firstPerson;
    private User secondPerson;

    public Conversation() {

    }

    public Conversation(User firstPerson, User secondPerson, String uid) {
        this.listMessageData = new ArrayList<>();
        this.firstPerson = firstPerson;
        this.secondPerson = secondPerson;
        this.uid = uid;
    }

    public ArrayList<ChatMessage> getListMessageData() {
        return listMessageData;
    }

    public void setListMessageData(ArrayList<ChatMessage> listMessageData) {
        this.listMessageData = listMessageData;
    }

    public User getFirstPerson() {
        return firstPerson;
    }

    public void setFirstPerson(User firstPerson) {
        this.firstPerson = firstPerson;
    }

    public User getSecondPerson() {
        return secondPerson;
    }

    public void setSecondPerson(User secondPerson) {
        this.secondPerson = secondPerson;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
