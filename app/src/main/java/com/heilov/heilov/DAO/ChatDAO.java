package com.heilov.heilov.DAO;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heilov.heilov.Model.ChatMessage;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.User;

import java.util.ArrayList;

public class ChatDAO {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private DatabaseReference ref;

    public ChatDAO() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("server/saving-data/userdata/chats");
    }

    public void getChats(MessagesCallback messagesCallback) {
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Conversation> chats = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.getKey().contains(currentUser.getUid())) {
                        chats.add(singleSnapshot.getValue(Conversation.class));
                    }
                }
                messagesCallback.onCallback(chats);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getConversation(String uid, ChatsCallback chatCallBack) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.getKey().equals(uid)) {
                        chatCallBack.onCallback(singleSnapshot.getValue(Conversation.class));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveNewChat(User u1, User u2) {

        ref.child(u1.getUid() + u2.getUid()).setValue(new Conversation(u1, u2, u1.getUid() + u2.getUid()));
    }

    public void addMessages(String uid, ChatMessage message) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.getKey().equals(uid)) {
                        Conversation c = singleSnapshot.getValue(Conversation.class);
                        ArrayList<ChatMessage> messages = c.getListMessageData();
                        messages.add(message);
                        c.setListMessageData(messages);
                        addNewMessage(c);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewMessage(Conversation conversation) {

        ref.child(conversation.getUid()).setValue(conversation);
    }
}
