package com.hackydesk.kavach_womensafetyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class firsttime extends AppCompatActivity {
CardView womenMode , policeMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firsttime);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        womenMode = findViewById(R.id.womenmode);
        policeMode = findViewById(R.id.policemode);

        womenMode.setOnClickListener(v -> {
            Intent intent = new Intent(firsttime.this, Login_Page.class);
            startActivity(intent);
        });

        policeMode.setOnClickListener(v -> {
            Intent intent = new Intent(firsttime.this, PoliceMode.class);
            startActivity(intent);
        });
    }

}