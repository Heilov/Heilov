package com.heilov.heilov.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heilov.heilov.DAO.UserCallback;
import com.heilov.heilov.DAO.UserDAO;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;
import com.heilov.heilov.Utils.EmailNotifier;
import com.heilov.heilov.Utils.InAppNotifier;
import com.heilov.heilov.Utils.Observer;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    UserDAO userDAO;
    FirebaseUser currentUser;

    EditText name;
    EditText location;
    EditText age;
    Spinner gender;
    Button saveProfile;
    User loggedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editprofile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = EditProfileActivity.this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(EditProfileActivity.this, R.color.colorPrimaryDark));
        setTitle("Edit Profile");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        saveProfile = findViewById(R.id.saveProfile);
        name = findViewById(R.id.edit_name);
        age = findViewById(R.id.edit_Age);
        location = findViewById(R.id.edit_location);
        gender = findViewById(R.id.gender_spinner);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userDAO = new UserDAO();
        userDAO.getUser(currentUser.getEmail(), new UserCallback() {
            @Override
            public void onCallback(User u) {
                Glide.with(EditProfileActivity.this).load(u.getProfilePic()).into((ImageView) findViewById(R.id.imageview_account_profile));
                name.setText(u.getName());
                age.setText(u.getAge() + "");
                location.setText(u.getLocation());
                loggedUser = u;
            }

            @Override
            public void onCallback(List<User> userList) {

            }
        });

        saveProfile.setText("Save Profile");

        saveProfile.setOnClickListener(v -> {
            loggedUser.setName(name.getText().toString());
            loggedUser.setAge(Integer.parseInt(age.getText().toString()));
            loggedUser.setLocation(location.getText().toString());
            loggedUser.setGender(gender.getSelectedItem().toString());
            userDAO.saveUser(loggedUser);


            Observer o1 = new InAppNotifier();
            Observer o2 = new EmailNotifier();
            loggedUser.attachObserver(o1);
            loggedUser.attachObserver(o2);
            loggedUser.notify(this, "Profile edited!");
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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
}
