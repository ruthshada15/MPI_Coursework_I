package com.example.redaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redaapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";
    private Button login;
 private EditText stremail, strpassword;
 private TextView signup;
 private SignInButton googlesignin;

 private FirebaseAuth mAuth;
 private FirebaseAuth.AuthStateListener mAuthListener;

 private static final int RC_SIGN_IN = 1;

 private GoogleApiClient mGoogleSignInClient;



 private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){

                    startActivity(new Intent(getApplicationContext(), FillInActivity.class));

                }

            }
        };

        stremail = (EditText) findViewById(R.id.txtemail);
        strpassword = (EditText) findViewById(R.id.txtpassword);
        login = (Button) findViewById(R.id.btnlogin);
        signup = (TextView) findViewById(R.id.btnsignup);
        googlesignin = (SignInButton) findViewById(R.id.googlesignin);


        loadingBar = new ProgressDialog(this);

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(getApplicationContext(), "You got an Error here!",Toast.LENGTH_LONG).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChooseActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = stremail.getText().toString().trim();
                String password = strpassword.getText().toString().trim();

                LogIn(email,password);
            }
        });

        //google signin OnClickListener

        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    } //end of onCreate



    private void LogIn (String a, String b){
        if (TextUtils.isEmpty(a)) {
            stremail.setError("Enter your Email Adress");
            stremail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher((CharSequence) a).matches()) {
            stremail.setError("Enter a Valid* Email Adress");
            stremail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(b)) {
            strpassword.setError("Enter your Password");
            strpassword.requestFocus();
            return;
        }
        if (b.length() < 6) {
            strpassword.setError("Password is too short");
            strpassword.requestFocus();
            return;
        }

        else {
            loadingBar.setTitle("User Login");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(a, b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String id = mAuth.getCurrentUser().getUid();


                    if (task.isSuccessful()) {
                        // Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    } else {
                        // Toast.makeText(getApplicationContext(), "nooo", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }
    }

    //GOOGLE SIGN IN

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle (GoogleSignInAccount account ){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");


                           /* FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);  COMMENTED COZ I DON'T NEED THIS*/


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();

                            // updateUI(null); THIS NEITHER
                        }

                        // ...
                    }
                });


    }
}
