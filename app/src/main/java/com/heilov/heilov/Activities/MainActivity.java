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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.heilov.heilov.DAO.ChatDAO;
import com.heilov.heilov.DAO.GalleryDAO;
import com.heilov.heilov.DAO.UserCallback;
import com.heilov.heilov.DAO.UserDAO;
import com.heilov.heilov.Model.ChatMessage;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.Photo;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private User loggedUser;

    private UserDAO userDAO;
    private GalleryDAO galleryDAO;
    private GoogleApiClient mGoogleApiClient;

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
        userDAO.getUser(currentUser.getEmail(), u -> {
            loggedUser = u;
            Glide.with(MainActivity.this).load(loggedUser.getProfilePic()).into((ImageView) findViewById(R.id.imageView));

            TextView email = findViewById(R.id.nameText);
            email.setText(loggedUser.getName());

            User u2 = new User("Florian Adelin", "adelin34@hotmail.com", "https://lookaside.facebook.com/platform/profilepic/?asid=2432840303408849&height=200&width=200&ext=1527161078&hash=AeSFWMIpSAot0Kva", "eNNEb00WOVdcN7p2dPjgslSjtkA3");
            ChatDAO chatDAO = new ChatDAO();
            chatDAO.saveNewChat(u, u2);

            ArrayList<ChatMessage> chatMessages = new ArrayList<>();
            chatMessages.add(new ChatMessage("Test", u2));
            chatMessages.add(new ChatMessage("Test2", u2));
            chatMessages.add(new ChatMessage("Test3", u));
            Conversation c = new Conversation();
            c.setFirstPerson(u);
            c.setSecondPerson(u2);
            c.setListMessageData(chatMessages);
            c.setUid(u.getUid() + u2.getUid());
            chatDAO.addNewMessage(c);
        });
/*
De aici am schimbat eu chestii
 */
        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You got a match!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                PopupWindow mPopupWindow;
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.show_user,null);

                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }
                RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_profile);

                userDAO.getRandomUser(new UserCallback() {
                    @Override
                    public void onCallback(User u) {
                        loggedUser = u;

                    }
                });

                Glide.with(MainActivity.this).load(loggedUser.getProfilePic()).into((ImageView) customView.findViewById(R.id.user_IMG));
                TextView user_name = customView.findViewById(R.id.user_name);
                TextView user_age = customView.findViewById(R.id.user_age);
                user_name.setText(loggedUser.getName());
                user_age.setText(String.valueOf(loggedUser.getAge()));
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

                new CountDownTimer(10000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        Snackbar.make(view, "You can chat now!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        new CountDownTimer(2000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                Snackbar.make(view, "Chat now! Do not miss the chance to fill your bed!!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                            @Override
                            public void onFinish() {
                                // TODO Auto-generated method stub
                            }
                        }.start();
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub

                        mPopupWindow.dismiss();
                    }
                }.start();

            }
        });
/*
pana aici am schimbat eu chestii
 */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            /*
            Deschid galeria si mai jos in onActivityResult(int requestCode, int resultCode, Intent data) incarc poza
             */
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

