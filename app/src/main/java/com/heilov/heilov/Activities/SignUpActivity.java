package com.heilov.heilov.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.heilov.heilov.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by adeli on 4/26/2018.
 */

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private Button btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 9001;
    private TwitterLoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    private String TAG = "HeiLov";

    private String avatarURL = "notFound";
    private GoogleApiClient mGoogleApiClient;
    private Bundle picBundle = new Bundle();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);


        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();


        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //   updateUI(user);
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("742458317466-4c6euho6qc15v0n9tpkuc058no9q8kk5.apps.googleusercontent.com")

                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_buttonGoogle).setOnClickListener(this);

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });


        Button btnSignIn = findViewById(R.id.sign_in_button);
        Button btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);


        //Twitter
        mLoginButton = findViewById(R.id.login_buttonTwitter);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {

                // updateUI(null);
            }
        });


        btnSignIn.setOnClickListener(v -> {

            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                    });

        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthStateListener);
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            auth.removeAuthStateListener(mAuthStateListener);
        }
        mGoogleApiClient.disconnect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            Log.w(TAG, "onActivityResult2");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);


            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.w(TAG, "onActivityResult");
                assert account != null;
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                //   updateUI(null);
                Log.w(TAG, "onActivityResultFail");
                // [END_EXCLUDE]
            }
        }


        // Pass the activity result to the Twitter login button.
        mLoginButton.onActivityResult(requestCode, resultCode, data);

        //Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleTwitterSession(TwitterSession session) {

        final AuthCredential credential;
        credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);


        //Getting the account service of the user logged in


        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(true, false, true).enqueue(new retrofit2.Callback<com.twitter.sdk.android.core.models.User>() {
                            @Override
                            public void onResponse(Call<com.twitter.sdk.android.core.models.User> call, Response<com.twitter.sdk.android.core.models.User> response) {
                                if (response.isSuccessful()) {
                                    //If it succeeds creating a User object from userResult.data
                                    com.twitter.sdk.android.core.models.User user;
                                    user = response.body();
                                    //Getting the profile image url
                                    avatarURL = user.profileImageUrl.replace("_normal", "");
                                    FirebaseUser currentUser = auth.getCurrentUser();
                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference("server/saving-data/userdata");
                                    DatabaseReference usersRef = ref.child("users");
                                    usersRef.child("user").setValue(new com.heilov.heilov.Model.User(currentUser.getDisplayName(), currentUser.getEmail(), avatarURL));

                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });


                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.putExtra("avatar", avatarURL);
                        startActivity(intent);
                        // Sign in success, update UI with the signed-in user's information
                        // FirebaseUser user = auth.getCurrentUser();


                    } else

                    {
                        Log.d(TAG, "Twitter failed");
                        // If sign in fails, display a message to the user.
                        // Toast.makeText(TwitterLoginActivity.this, "Authentication failed.",
                        //Toast.LENGTH_SHORT).show();
                        //  updateUI(null);
                    }

                    // ...
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        getFacebookData();
//                        Log.d(TAG, picBundle.getString("avatar"));
//                        picBundle.getString("avatar");
                        startActivity(intent);
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        // updateUI(null);
                    }

                    // ...
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.w(TAG, "firebaseAuthWithGoogle:succed");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.

                    if (!task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    } else {
                        Log.w(TAG, "signInWithCredential:succed");
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void signIn() {
        Log.w(TAG, "signIn:succed");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "connection:failure");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_buttonGoogle) {
            signIn();
        }
    }


    private void getFacebookData() {
        String FACEBOOK_FIELD_PROFILE_IMAGE = "picture.type(large)";
        String FACEBOOK_FIELDS = "fields";

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> updateAvatar(getImageUrl(response)));
        Bundle parameters = new Bundle();
        parameters.putString(FACEBOOK_FIELDS, FACEBOOK_FIELD_PROFILE_IMAGE);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private String getImageUrl(GraphResponse response) {
        String url = null;
        String FACEBOOK_FIELD_PICTURE = "picture";
        String FACEBOOK_FIELD_DATA = "data";
        try {
            String FACEBOOK_FIELD_URL = "url";
            url = response.getJSONObject()
                    .getJSONObject(FACEBOOK_FIELD_PICTURE)
                    .getJSONObject(FACEBOOK_FIELD_DATA)
                    .getString(FACEBOOK_FIELD_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseUser currentUser = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/userdata");
        DatabaseReference usersRef = ref.child("users");
        usersRef.child("user").setValue(new com.heilov.heilov.Model.User(currentUser.getDisplayName(), currentUser.getEmail(), url));



        return url;
    }


    public void updateAvatar(String url) {
        this.avatarURL = url;

    }
}
