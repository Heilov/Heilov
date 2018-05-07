package com.heilov.heilov.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heilov.heilov.R;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

/**********    AM INCERCAT SA IAU DATE (POZA SI EMAIL) DIN CONTUL DE TWITTER, NU MERGE DEOCAMDATA     ********************
 *
 final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
 navigationView.setNavigationItemSelectedListener(this);

 Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, false);
 user.enqueue(new Callback<User>() {
@Override public void success(Result<User> userResult) {
String name = userResult.data.name;
String email = userResult.data.email;
// _normal (48x48px) | _bigger (73x73px) | _mini (24x24px)
String photoUrlNormalSize   = userResult.data.profileImageUrl;
String photoUrlBiggerSize   = userResult.data.profileImageUrl.replace("_normal", "_bigger");
String photoUrlMiniSize     = userResult.data.profileImageUrl.replace("_normal", "_mini");
String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");


TextView textView = navigationView.findViewById(R.id.nameText);
textView.setText(name);
//textView = navigationView.findViewById(R.id.emailText);
//textView.setText(email);

//ImageView imageView = navigationView.findViewById(R.id.imageView);
//imageView.setImageURI(Uri.parse(photoUrlMiniSize));
}

@Override public void failure(TwitterException exc) {
//Log.d("TwitterKit", "Verify Credentials Failure", exc);
}@Override public void failure(TwitterException exc) {
//Log.d("TwitterKit", "Verify Credentials Failure", exc);
}
});
 *
 ****************************************************/

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
            Log.w("2Sign", "onActivityResultFail");
            auth = FirebaseAuth.getInstance();
            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        if (id == R.id.nav_logout) {
            Log.w("1Sign", "onActivityResultFail");
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

