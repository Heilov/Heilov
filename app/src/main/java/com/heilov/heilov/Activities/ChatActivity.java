package com.heilov.heilov.Activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heilov.heilov.DAO.ChatDAO;
import com.heilov.heilov.DAO.ChatsCallback;
import com.heilov.heilov.DAO.UserCallback;
import com.heilov.heilov.DAO.UserDAO;
import com.heilov.heilov.Model.ChatMessage;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;
import com.heilov.heilov.Utils.MessageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ChatDAO chatDAO;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private EditText text;
    private UserDAO userDAO;
    private FirebaseUser currentUser;
    private String uid;

    private boolean oldMessagesAdded = false;
    private ArrayList<ChatMessage> newMessages;
    private String otherPers;
    private Conversation currentConversation;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = ChatActivity.this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(ChatActivity.this, R.color.colorPrimaryDark));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        LinearLayout t = findViewById(R.id.layout_chatbox);
        t.bringToFront();
        setTitle(getIntent().getStringExtra("name"));
        text = findViewById(R.id.edittext_chatbox);
        newMessages = new ArrayList<>();
        otherPers = getIntent().getStringExtra("chatter");
        uid = getIntent().getStringExtra("uid");
        Button send = findViewById(R.id.messenger_send_button);
        userDAO = new UserDAO();
        chatDAO = new ChatDAO();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        userDAO.getUser(currentUser.getEmail(), new UserCallback() {
            @Override
            public void onCallback(User u) {
                loggedUser = u;
            }

            @Override
            public void onCallback(List<User> userList) {

            }
        });

        chatDAO.getConversation(uid, new ChatsCallback() {
            @Override
            public void onCallback(Conversation u) {
                currentConversation = u;
                displayConversation(currentConversation);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text.getText().toString();
                if (s.length() > 0) {
                    if (currentConversation.getListMessageData() != null) {
                        currentConversation.getListMessageData().add(new ChatMessage(s, loggedUser));
                        displayConversation(currentConversation);
                        text.getText().clear();
                        chatDAO.addNewMessage(currentConversation);
                    } else {
                        ArrayList<ChatMessage> mess = new ArrayList<>();
                        mess.add(new ChatMessage(s, loggedUser));
                        currentConversation.setListMessageData(mess);
                        displayConversation(currentConversation);
                        text.getText().clear();
                        chatDAO.addNewMessage(currentConversation);
                    }
                }

            }
        });
    }

    private void displayConversation(Conversation currentConversation) {
        if (currentConversation.getListMessageData() != null) {
            newMessages = currentConversation.getListMessageData();
            mMessageRecycler = findViewById(R.id.reyclerview_message_list);
            mMessageAdapter = new MessageListAdapter(ChatActivity.this, newMessages);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
            mMessageRecycler.setAdapter(mMessageAdapter);
            mMessageRecycler.scrollToPosition(newMessages.size() - 1);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
