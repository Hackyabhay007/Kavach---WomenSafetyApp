package com.hackydesk.kavach_womensafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackydesk.kavach_womensafetyapp.kit.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class register extends AppCompatActivity {
    TextView redirect_login;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String currentDate;
    String currentTime;
    String Fname,Email,Phonenumber,Aadhar,Password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button register_button;
    ProgressDialog pd;
    EditText fname , email , phonenumber , aadhar,password;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fname = findViewById(R.id.fullname);
        email = findViewById(R.id.user_email);
        phonenumber = findViewById(R.id.phonenumber);
        aadhar = findViewById(R.id.user_aadhar);
        password = findViewById(R.id.user_pwd);
        register_button = findViewById(R.id.register_button);
        redirect_login = findViewById(R.id.redirect_register);


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fname.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phonenumber.getText().toString().isEmpty() || password.getText().toString().isEmpty() || aadhar.getText().toString().isEmpty()) {

                    Toast.makeText(register.this, "Please fill all the feilds", Toast.LENGTH_SHORT).show();
                } else if (phonenumber.getText().toString().length() < 10) {

                    Toast.makeText(register.this, "Input Atleast 10 Digit in Phone Number", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().length() < 6) {

                    Toast.makeText(register.this, "Password Should Have Atleaset 6 Digits", Toast.LENGTH_SHORT).show();
                }
                else if (aadhar.getText().toString().length() < 12 &&aadhar.getText().toString().length()!= 12 ) {

                    Toast.makeText(register.this, "Aadhar number  Should Have Only 12 Digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(register.this, "Registering", Toast.LENGTH_SHORT).show();
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("fullname",fname.getText().toString());
                                    updates.put("email", email.getText().toString());
                                    updates.put("phonenumber", phonenumber.getText().toString());
                                    updates.put("aadhar", aadhar.getText().toString());
                                    Toast.makeText(register.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    Tools tools = new Tools(register.this,getApplicationContext());
                                    tools.savetoDb("authuserid",tools.currentUserid());
                                    db.collection("Users").document((Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))).set(updates);
                                    Intent intent = new Intent(register.this, Home.class);
                                    startActivity(intent);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        redirect_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register.this, Login_Page.class);
                startActivity(intent);
            }
        });
    }
    }
