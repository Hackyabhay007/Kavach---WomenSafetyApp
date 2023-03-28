package com.hackydesk.kavach_womensafetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private  int DELAY = 1000;
    private static final int TIME_INTERVAL = 2000; // Time interval for double back press in milliseconds
    private long backPressedTime;

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
                Intent intent = new Intent(MainActivity.this, firsttime.class);
                startActivity(intent);
            }
        }, DELAY);

    }
    public void onBackPressed() {
        if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}