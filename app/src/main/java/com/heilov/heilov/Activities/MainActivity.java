package com.heilov.heilov.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.heilov.heilov.DAO.ChatDAO;
import com.heilov.heilov.DAO.GalleryDAO;
import com.heilov.heilov.DAO.MessagesCallback;
import com.heilov.heilov.DAO.UserCallback;
import com.heilov.heilov.DAO.UserDAO;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;
import com.heilov.heilov.Utils.EmailNotifier;
import com.heilov.heilov.Utils.InAppNotifier;
import com.heilov.heilov.Utils.Observer;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    private User loggedUser, matchUser;
    private ChatDAO chatDAO;
    private UserDAO userDAO;
    private GalleryDAO galleryDAO;
    private GoogleApiClient mGoogleApiClient;
    private PopupWindow mPopupWindow;
    private ArrayList<User> usersArrayList;
    private int[] imgIDS = new int[]{R.id.user0, R.id.user1, R.id.user2, R.id.user3, R.id.user4, R.id.user5};
    private DatabaseReference mDatabase;
    private ArrayList<Conversation> usersConversations;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        galleryDAO = new GalleryDAO();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserDAO userDAO = new UserDAO();

        userDAO.getUser(currentUser.getEmail(), new UserCallback() {
            @Override
            public void onCallback(User u) {
                loggedUser = u;
                Glide.with(MainActivity.this).load(loggedUser.getProfilePic()).into((ImageView) findViewById(R.id.imageView));

                TextView name = findViewById(R.id.nameText);
                name.setText(loggedUser.getName());
            }

            @Override
            public void onCallback(List<User> userList) {

            }
        });

        userDAO.getUsers(new UserCallback() {
            @Override
            public void onCallback(User u) {

            }

            @Override
            public void onCallback(List<User> userList) {
                usersArrayList = (ArrayList<User>) userList;

                if (usersArrayList.size() < 6) {
                    int i = 0;
                    for (User u : usersArrayList) {
                        Glide.with(MainActivity.this).load(u.getProfilePic()).into((ImageView) findViewById(imgIDS[i]));

                    }
                } else {
                    for (int i = 0; i < 6; i++) {
                        Glide.with(MainActivity.this).load(usersArrayList.get(i).getProfilePic()).into((ImageView) findViewById(imgIDS[i]));

                    }
                }
            }
        });

        chatDAO = new ChatDAO();
        chatDAO.getChats(new MessagesCallback() {
            @Override
            public void onCallback(ArrayList<Conversation> u) {
                usersConversations = u;
            }
        });

/*
De aici am schimbat eu chestii
 */
        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener((View view) -> {
            getMatch(view);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMatch(View view) {
        Snackbar.make(view, "You got a match!!!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.show_user, null);

        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        RelativeLayout mRelativeLayout = findViewById(R.id.activity_profile);

        boolean matched = false;
        int x = 0;
        while (!matched) {
            Random ran = new Random();
            x = ran.nextInt(usersArrayList.size() - 1);
            String matchUID = usersArrayList.get(x).getUid();
            String uid1 = loggedUser.getUid() + matchUID;
            String uid2 = matchUID + loggedUser.getUid();

            for (Conversation c : usersConversations) {
                if (!c.getUid().equals(uid1) && !c.getUid().equals(uid2)) {
                    matched = true;
                }
            }
        }
        matchUser = usersArrayList.get(x);
        Glide.with(MainActivity.this).load(matchUser.getProfilePic()).into((ImageView) customView.findViewById(R.id.user_IMG));
        TextView user_name = customView.findViewById(R.id.user_name);
        TextView user_age = customView.findViewById(R.id.user_age);
        user_name.setText("Name: " + matchUser.getName());
        user_age.setText("Age: " + String.valueOf(matchUser.getAge()));
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 140);
        Snackbar.make(view, "You can chat now!!!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        Observer o1 = new InAppNotifier();
        Observer o2 = new EmailNotifier();
        loggedUser.attachObserver(o1);
        loggedUser.attachObserver(o2);
        loggedUser.notify(this, "Got a match");



        chatDAO.saveNewChat(loggedUser, matchUser);
        chatDAO.getChats(u -> usersConversations = u);

        new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                mPopupWindow.dismiss();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.nav_logout) {
            auth = FirebaseAuth.getInstance();
            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Intent gallery = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 1);

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this, GalleryActivity.class));

        } else if (id == R.id.nav_messages) {
            startActivity(new Intent(MainActivity.this, MessagesActivity.class));
        } else if (id == R.id.nav_editProfile) {
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_logout) {

            auth = FirebaseAuth.getInstance();

            if (AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null) {
                LoginManager.getInstance().logOut();
            }

            TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
            if (twitterSession != null) {
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }

            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    LOAD image din telefon
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = findViewById(R.id.imageView);

            Bitmap bmp = null;
            Bitmap scaled = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
                int nh = (int) (bmp.getHeight() * (200.0 / bmp.getWidth()));
                scaled = Bitmap.createScaledBitmap(bmp, 200, nh, true);
            } catch (IOException e) {

                e.printStackTrace();
            }
            galleryDAO.savePhoto(scaled);

        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}

