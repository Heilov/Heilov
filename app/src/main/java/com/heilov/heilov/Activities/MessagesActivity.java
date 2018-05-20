package com.heilov.heilov.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heilov.heilov.DAO.ChatCallBack;
import com.heilov.heilov.DAO.ChatDAO;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;
import com.heilov.heilov.Utils.ChatsAdapter;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {
    private ChatDAO chatDAO;
    private FirebaseAuth auth;
    private ListView lw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setTitle("Messages");
        chatDAO = new ChatDAO();
        lw = findViewById(R.id.listMessages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = MessagesActivity.this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(MessagesActivity.this, R.color.colorPrimaryDark));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getUserChats();

        lw.setOnItemClickListener((parent, view, position, id) -> {
            Conversation entry = (Conversation) parent.getAdapter().getItem(position);
            Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);

            auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (entry.getFirstPerson().getUid().equals(currentUser.getUid())) {
                intent.putExtra("chatter", entry.getSecondPerson().getEmail());
                intent.putExtra("name",entry.getSecondPerson().getName());
            } else {
                intent.putExtra("chatter", entry.getFirstPerson().getEmail());
                intent.putExtra("name",entry.getFirstPerson().getName());
            }
            intent.putExtra("uid",entry.getUid());
            startActivity(intent);
        });

    }

    private void getUserChats() {
        chatDAO.getChats(u -> lw.setAdapter(new ChatsAdapter(this, u)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
