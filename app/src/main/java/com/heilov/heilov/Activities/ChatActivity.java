package com.heilov.heilov.Activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heilov.heilov.DAO.ChatDAO;
import com.heilov.heilov.DAO.UserDAO;
import com.heilov.heilov.Model.ChatMessage;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;
import com.heilov.heilov.Utils.MessageListAdapter;

import java.util.ArrayList;
import java.util.Comparator;

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
        assert currentUser != null;
        userDAO.getUser(currentUser.getEmail(), u -> chatDAO.getConversation(uid, u1 -> {
            ArrayList<ChatMessage> messages = u1.getListMessageData();
            newMessages.addAll(messages);
            mMessageRecycler = findViewById(R.id.reyclerview_message_list);

            mMessageAdapter = new MessageListAdapter(ChatActivity.this, messages);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mMessageRecycler.setAdapter(mMessageAdapter);
            mMessageRecycler.scrollToPosition(newMessages.size() - 1);
        }));

        send.setOnClickListener(v -> {
            String s = text.getText().toString();
            if (s.length() > 0) {

                userDAO.getUser(currentUser.getEmail(), (User u) -> {
                    newMessages.add(new ChatMessage(s, u));
                    if (!oldMessagesAdded) {
                        chatDAO.getConversation(uid, u12 -> {
                            ArrayList<ChatMessage> aux = u12.getListMessageData();
                            for (ChatMessage cm : aux) {
                                if (newMessages.contains(cm)) {
                                    newMessages.add(cm);
                                }
                            }
                            newMessages.sort(Comparator.comparingLong(ChatMessage::getMessageTime));
                            oldMessagesAdded = true;
                        });
                    }

                    mMessageAdapter = new MessageListAdapter(ChatActivity.this, newMessages);
                    mMessageRecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    mMessageRecycler.setAdapter(mMessageAdapter);
                    mMessageRecycler.scrollToPosition(newMessages.size() - 1);
                });

            }
            text.getText().clear();
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            userDAO.getUser(currentUser.getEmail(), u -> userDAO.getUser(otherPers, u1 -> {
                ChatDAO ch = new ChatDAO();
                Conversation c = new Conversation(u, u1, uid);
                c.setListMessageData(newMessages);
                ch.addNewMessage(c);
            }));

            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
