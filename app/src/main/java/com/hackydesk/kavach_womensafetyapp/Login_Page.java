package com.hackydesk.kavach_womensafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.hackydesk.kavach_womensafetyapp.kit.Tools;

public class Login_Page extends AppCompatActivity {


    TextView redirect_register;
    EditText email , password;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        redirect_register = findViewById(R.id.redirect_register);
        Button button = findViewById(R.id.register_button);
        email = findViewById(R.id.email_);
        password = findViewById(R.id.passwprd);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ) {

                    Toast.makeText(Login_Page.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login_Page.this, "Login Success", Toast.LENGTH_SHORT).show();
                            Tools tools = new Tools(Login_Page.this,getApplicationContext());
                            tools.savetoDb("authuserid",tools.currentUserid());
                            startActivity(new Intent(Login_Page.this,Home.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login_Page.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        redirect_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Page.this, register.class);
                startActivity(intent);
            }
        });

    }

}