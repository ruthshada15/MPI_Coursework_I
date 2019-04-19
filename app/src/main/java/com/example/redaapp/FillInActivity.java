package com.example.redaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;

public class FillInActivity extends AppCompatActivity {

    private EditText strfname, strlname, strphonenum;
    private Button glogin;

    private ImageView customerprofilepic;
    private Uri imageUri = null;
    private final int select_photo = 1;

    private ProgressDialog loadingBar;

    private FirebaseAuth myAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in);

        strfname = (EditText) findViewById(R.id.gtxtcustomerfname);
        strlname = (EditText) findViewById(R.id.gtxtcustomerlname);
        strphonenum = (EditText) findViewById(R.id.gtxtcustomerphonenumber);
        glogin = (Button) findViewById(R.id.gbtnlogin);
        customerprofilepic = (ImageView) findViewById(R.id.gcustomerprofilepicture);

        glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String fname = strfname.getText().toString().trim();
                String lname = strlname.getText().toString().trim();
                String phonenum = strphonenum.getText().toString().trim();


                validation(fname, lname, phonenum);


            }
        });
        onclickimage();

    }


    private void validation(String c, String d, String e) {

        if (TextUtils.isEmpty(c)) {
            strfname.setError("Enter your First Name");
            strfname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(d)) {
            strlname.setError("Enter your Last Name");
            strlname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(e)) {
            strphonenum.setError("Enter your Phone Number");
            strphonenum.requestFocus();
            return;
        } else {
            loadingBar.setTitle("User Registration");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();

            loadingBar.dismiss();

            saveCustomerInformation();

            startActivity(new Intent(getApplicationContext(), MapsActivity.class));


        }

    }

    private void saveCustomerInformation() {

        String fname = strfname.getText().toString().trim();
        String lname = strlname.getText().toString().trim();
        String phonenum = strphonenum.getText().toString().trim();

        CustomerInformation customerInformation = new CustomerInformation(fname, lname, phonenum);
        FirebaseUser user = myAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(customerInformation);

    }


    //selecting and displaying image

    private void onclickimage() {
        customerprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in, select_photo);
            }
        });
    }


    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);

        if (requestcode == select_photo) {
            if (resultcode == RESULT_OK) {
                try {

                    imageUri = imagereturnintent.getData();

                    Bitmap bitmap = Utils.decodeUri(getApplicationContext(),
                            imageUri, 200);// call

                    if (bitmap != null) {
                        customerprofilepic.setImageBitmap(bitmap);


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error while decoding image.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "File not found.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
