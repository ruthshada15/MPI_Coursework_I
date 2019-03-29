package com.example.roda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerSignupActivity extends AppCompatActivity {
    private EditText stremail, strpassword;
    private Button getstarted;

    private ProgressDialog loadingBar;

    FirebaseAuth myAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        myAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        loadingBar = new ProgressDialog(this);

        stremail = (EditText) findViewById(R.id.signuptxtemail);
        strpassword = (EditText) findViewById(R.id.signuptxtpassword);
        getstarted = (Button) findViewById(R.id.btngetstarted);

        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = stremail.getText().toString().trim();
                String password = strpassword.getText().toString().trim();

                validation(email,password);



            }
        });
    }



    private void validation(String a, String b) {
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
            loadingBar.setTitle("User Registration");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();
            myAuth.createUserWithEmailAndPassword(a, b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String id = myAuth.getCurrentUser().getUid();


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
}
