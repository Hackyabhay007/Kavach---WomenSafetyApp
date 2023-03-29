package com.hackydesk.kavach_womensafetyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hackydesk.kavach_womensafetyapp.kit.Tools;
import com.hackydesk.kavach_womensafetyapp.kit.bottomsheetDialog;

public class police_dashboard extends AppCompatActivity implements OnMapReadyCallback {
    DatabaseReference Status;
    FirebaseFirestore db;
    DocumentReference dbfetcher;
    String userId;
    GoogleMap Mymap;
    double latitude,longitude;
    Tools t1;
    Button callbtn;
    TextView alert_date, alert_time, alert_accuracy, alert_speed, alert_battery, addressBtnText,Distance_alert,User_aadhaar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_dashboard);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        alert_date = findViewById(R.id.data_fetchdate);
        alert_time = findViewById(R.id.data_fetch_time);
        alert_battery = findViewById(R.id.data_percentage);
        alert_speed = findViewById(R.id.alert_speed);
        Distance_alert = findViewById(R.id.alert_distance);
        User_aadhaar = findViewById(R.id.Aadhaar_user);


        Button OpenBottomSheet = findViewById(R.id.open_bottom_sheet);
        t1 = new Tools(police_dashboard.this,getApplicationContext());
        userId = t1.getData("useridForPolice").trim();

        bottomsheetDialog bottomSheet = new bottomsheetDialog();
       OpenBottomSheet.setOnClickListener(v -> {
           bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
       });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Tools t1 = new Tools(police_dashboard.this,getApplicationContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
         Status = database.getReference("UsersStatusData").child(t1.getData("useridForPolice")).child("DANGERMODE");
    }
    void SOSupdates()
    {
        //gets status of child
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //  sosStatus =true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        Status.addValueEventListener(postListener);
    }
    void LiveListener() {
        db = FirebaseFirestore.getInstance();
        //Toast.makeText(this, , Toast.LENGTH_SHORT).show();
        dbfetcher = db.collection("Users").document(userId);
        dbfetcher.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                Toast.makeText(police_dashboard.this, String.valueOf("lat"), Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: " + snapshot.getData());
               double lat = Double.parseDouble(String.valueOf(snapshot.get("lat")));
               double lng = Double.parseDouble(String.valueOf(snapshot.get("lng")));
               latitude =lat;
               longitude = lng;
                String Alert_battery = String.valueOf(snapshot.get("BatteryStatus"));
                String Alert_speed = String.valueOf(snapshot.get("Device Speed"));
                String Alert_accuracy = String.valueOf(snapshot.get("accuracy"));
                String date = String.valueOf(snapshot.get("date"));
                String time = String.valueOf(snapshot.get("time"));
                String name = String.valueOf(snapshot.get("fullname"));
                String phoneNumber = String.valueOf(snapshot.get("phonenumber"));
                String aadhaarId = String.valueOf(snapshot.get("aadhar"));
                double speed = Double.parseDouble(Alert_speed);
                speed = Math.round(speed);
                speed = ((speed * 3600) / 1000);
                Alert_speed = String.valueOf(speed);
                alert_time.setText(time.toUpperCase());
                alert_date.setText(date);
                alert_speed.setText(Alert_speed + "Kmph");
                alert_battery.setText(Alert_battery + "%");
                Distance_alert.setText(name);
                User_aadhaar.setText(aadhaarId);
                adddata(phoneNumber,String.valueOf(lat),String.valueOf(lng));

                Mymap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("User")
                        .snippet("Position Fetched"));
                //calculate distance
                mapUpdater(lat, lng);
            } else {
                Log.d("TAG", "Current data: null");
            }
        });
    }
    void mapUpdater(double lat, double lng) {
        Mymap.getUiSettings().setZoomGesturesEnabled(true);
        Mymap.getUiSettings().setRotateGesturesEnabled(true);
        LatLng position = new LatLng(lat, lng);
        //Mymap.addMarker(new MarkerOptions().position(position).title("Location Fetched"));
        Mymap.clear();
        Marker customMarker;customMarker = Mymap.addMarker(new MarkerOptions().position(position).title("User Position"));
          //  customMarker.setIcon(BitmapDescriptorFactory.fromResource(R));
        customMarker.setPosition(position);
        // Mymap.moveCamera(CameraUpdateFactory.newLatLng(position));
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Mymap = googleMap;
        mapUpdater(0,0);
        LiveListener();
    }
    void directonlinkgenerator() {
        dbfetcher = db.collection("Users").document(userId);
        dbfetcher.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                double Childlat = Double.parseDouble(String.valueOf(documentSnapshot.get("lat")));
                String Childlng = String.valueOf(documentSnapshot.get("lng"));
                //loader.dismissloader("Google Maps Direction", "Getting Accurate Location");
                Toast.makeText(getApplicationContext(), "Opening Direction to Last Location", Toast.LENGTH_SHORT).show();
                getDirections(Childlat, Double.parseDouble(Childlng));
            } else {
                Toast.makeText(getParent(), "Cant find Directions Try again", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(police_dashboard.this, "Last Location Not Found", Toast.LENGTH_SHORT).show());
    }

    public void getDirections(double lat, double lon) {
        t1.getdirections(latitude,longitude);
    }

    public void adddata(String USER_PHONENUMBER,String UserLat,String UserLng)
    {
        Tools t1 = new Tools(police_dashboard.this,getApplicationContext());
        t1.savetoDb("phoneNumberForPolice",USER_PHONENUMBER);
        t1.savetoDb("UserLat",UserLat);
        t1.savetoDb("UserLng",UserLng);
    }
}