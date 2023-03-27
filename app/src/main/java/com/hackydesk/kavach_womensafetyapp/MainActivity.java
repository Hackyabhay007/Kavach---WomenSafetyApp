package com.hackydesk.kavach_womensafetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private  int DELAY = 1000;

    EditText fname , email , phonenumber , aadhar,password;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Handler handler = new Handler();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        handler.postDelayed(() -> {
            if (user!=null)
            {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, Login_Page.class);
                startActivity(intent);
            }
        }, DELAY);

    }
}