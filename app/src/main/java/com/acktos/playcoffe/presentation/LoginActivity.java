package com.acktos.playcoffe.presentation;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.UsersController;
import com.acktos.playcoffe.models.User;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements
        ConnectionStateCallback, View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Spotify Credentials
    private static final String CLIENT_ID = "3dfc10d1cf314ce89bc992c8ac864052";
    private static final String REDIRECT_URI = "coffeplay-protocol://callback";

    //UI references
    private Button btnSpotifyLogin;
    private Button btnGoogleLogin;
    private TextView txtLoginStatus;
    private ProgressDialog mAuthProgressDialog;

    //Attributes
    private static final int REQUEST_CODE = 1325;

    //Components
    private AuthenticationRequest request;
    private UsersController usersController;
    private Firebase mFirebaseRef; //FireBase reference
    private Firebase.AuthStateListener mAuthStateListener;//Listener for Firebase session changes


    //Android Utils
    SharedPreferences mPrefs;// Handle to SharedPreferences for this APP
    SharedPreferences.Editor mEditor;// Handle to a SharedPreferences editor

    //Google API Client
    private static final int RC_SIGN_IN = 0;/* Request code used to invoke sign in user interactions. */
    private GoogleApiClient mGoogleApiClient;/* Client used to interact with Google APIs. */
    private boolean mIsResolving = false;/* Is there a ConnectionResult resolution in progress? */
    private boolean mShouldResolve = false;/* Should we automatically resolve ConnectionResults when possible? */
    private String googleAccessToken;



    /* *************************************
     *              GENERAL                *
     ***************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //Initialize UI
        btnSpotifyLogin=(Button) findViewById(R.id.btn_spotify_login);
        btnGoogleLogin=(Button) findViewById(R.id.btn_google_login);
        txtLoginStatus=(TextView) findViewById(R.id.txt_login_status);
        setupAuthProgressDialog();

        //Set clicks listener to UI
        btnSpotifyLogin.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);

        //Initialize components
        usersController=new UsersController(this);
        setupSpotifyLogin();
        setupFirebase();


        //initialize Android Utils
        mPrefs = getSharedPreferences(BaseController.SHARED_PREFERENCES, Context.MODE_PRIVATE);// Open Shared Preferences
        mEditor = mPrefs.edit();// Get an editor


        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        //UI changes
        //btnGooglogin.setColorScheme(SignInButton.COLOR_LIGHT);

    }

    @Override
    protected void onStart() {
        super.onStart();

        User user=usersController.getUser();

        if(user!=null){
            Intent i=new Intent(this,NavigationActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                Log.i(BaseController.TAG,"spotify access token:"+response.getAccessToken());

                setLoginStatus(getString(R.string.msg_get_spotify_user_status));
                usersController.getSpotifyUser(response.getAccessToken(), new Response.Listener<User>() {
                    @Override
                    public void onResponse(User user) {

                        if(user!=null){

                            signUpFirebaseUser(user);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        setLoginStatus(getString(R.string.msg_user_login_error_status));
                        Log.e(BaseController.TAG,"Error trying to retrieve spotify user data ");
                    }
                });

            }
        }

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        //mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_spotify_login:
                setLoginStatus(getString(R.string.connecting));
                AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
                break;

            case R.id.btn_google_login :
                onSignInClicked();
                break;

            default:
                break;
        }
    }


    private void setLoginStatus(String message){

        txtLoginStatus.setText(message);
    }

    private void setupAuthProgressDialog(){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.loading));
        mAuthProgressDialog.setMessage(getString(R.string.msg_loading_profile));
        mAuthProgressDialog.setCancelable(false);
    }

    private void launchNavigationView(){

        Intent i=new Intent(this,NavigationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }


    /* *************************************
     *              FIREBASE               *
     ***************************************/

    private void setupFirebase(){

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(BaseController.FIREBASE_URL);//Create the Firebase ref

    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.i(BaseController.TAG, provider + " auth successful");
            //signUpGoogleUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
            Log.i(BaseController.TAG,"code"+firebaseError.getCode()+" error:"+firebaseError.getMessage());
        }
    }

    private void  signUpGoogleUser(){


        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

            setLoginStatus(getString(R.string.msg_get_google_user_status));

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().toString();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String birthday=currentPerson.getBirthday();

            Log.i(BaseController.TAG, "google birthday:" + birthday);


            final User user=new User(personName,email,personPhoto,"google");
            user.setBirthdate(birthday);
            signUpFirebaseUser(user);


        }else{
            setLoginStatus(getString(R.string.msg_user_login_error_status));
            Log.e(BaseController.TAG, "failed get google person");
        }

    }

    private void signUpFirebaseUser(final User user){

        setLoginStatus(getString(R.string.msg_signup_user_status));
        final Firebase userRef=mFirebaseRef.child(BaseController.TABLE_USERS);
        final Firebase newUserRef=userRef.push();
        newUserRef.setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {

                    setLoginStatus(getString(R.string.msg_user_login_error_status));
                    Log.e(BaseController.TAG, "firebase error:" + firebaseError.getMessage());
                } else {
                    setLoginStatus(null);
                    Log.i(BaseController.TAG, "user saved successfully in Firebase:" + newUserRef.getKey());
                    String userId = newUserRef.getKey();
                    user.setId(userId);
                    usersController.saveUserProfile(user); // saves user in local storage

                    if(!TextUtils.isEmpty(userId)){
                        launchNavigationView();
                    }

                }
            }
        });


    }

    /* *************************************
     *              SPOTIFY                *
     ***************************************/

    private void setupSpotifyLogin() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "user-read-email", "user-read-birthdate"});
        request = builder.build();
    }

    @Override
    public void onLoggedIn() {
        Log.d(BaseController.TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(BaseController.TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d(BaseController.TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(BaseController.TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(BaseController.TAG, "Received connection message: " + message);
    }



    /* *************************************
     *              GOOGLE                 *
     ***************************************/

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(BaseController.TAG, "onConnected:" + bundle);
        mShouldResolve = false;
        setLoginStatus(null);

        signUpGoogleUser();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(BaseController.TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(BaseController.TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            setLoginStatus(null);
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        setLoginStatus(getString(R.string.connecting));
    }

    private void showErrorDialog(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mShouldResolve = false;
                            //updateUI(false);
                        }
                    }).show();
        } else {
            // No default Google Play Services error, display a message to the user.
            String errorString = "play services error"+ errorCode;
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

            mShouldResolve = false;
            //updateUI(false);
        }
    }

    /**
     *  Get OAuth token in Background
     */
    private void getGoogleOAuthToken() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected void onPreExecute() {
                mAuthProgressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {

                    String scope = String.format("oauth2:%s", Scopes.PROFILE);
                    return GoogleAuthUtil.getToken(LoginActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scope);

                } catch (IOException e) {
                    Log.e(BaseController.TAG, "IO Error retrieving ID token.", e);
                    return null;
                } catch (GoogleAuthException e) {
                    Log.e(BaseController.TAG, "Auth Error retrieving ID token."+e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String token) {
                Log.i(BaseController.TAG, "ID token: " + token);
                if (token != null) {
                    googleAccessToken=token;
                    mFirebaseRef.authWithOAuthToken("google", token, new AuthResultHandler("google"));
                } else {
                    mAuthProgressDialog.hide();
                    showErrorDialog(errorMessage);
                }

            }
        };
        task.execute();
    }


}
