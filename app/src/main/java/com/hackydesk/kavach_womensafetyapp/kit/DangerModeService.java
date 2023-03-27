package com.hackydesk.kavach_womensafetyapp.kit;



import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationRequest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackydesk.kavach_womensafetyapp.Home;
import com.hackydesk.kavach_womensafetyapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DangerModeService extends Service  {

    Context mContext;
    String  authuserid;
    Tools t1 = new Tools(getBaseContext());
    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationRequest locationRequest;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        notificationManager = getSystemService(NotificationManager.class);
        final String CHANNELID = "Kavach-Kit";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        Notification.Builder notification = null;
        Intent SafeHomeIntent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, SafeHomeIntent, PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNELID)
                    .setContentText("Initiated All Alerts Sharing Location And Device Status With Police")
                    .setContentTitle("SOS Mode enabled")
                    .setSmallIcon(R.drawable.female_avatar_girl_face_woman_user_2_svgrepo_com__2_)
                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.red))
                    .setColorized(true).setColor(Color.RED)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
        }
        assert notification != null;
        initiateDangermode();
        startForeground(1002, notification.build());
        return START_NOT_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }
    void initiateDangermode()
    {
        Toast.makeText(mContext, "STARTING SOS MODE ..", Toast.LENGTH_SHORT).show();
        deviceStatus();
    }
//current location check and location track service activator // online mode
    void deviceStatus() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
//Instantiating the Location request and setting the priority and the interval I need to update the location.
        locationRequest = locationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
//instantiating the LocationCallBack
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    if (locationResult == null) {
                        return;
                    }
                    //Showing the latitude, longitude and accuracy on the home screen.
                    for (Location location : locationResult.getLocations()) {
                        double lat =  location.getLatitude();
                        double lng =  location.getLongitude();
                        int BatteryPercentage=0 ;

                        // Toast.makeText(mContext, Double.toString(lat) + Double.toString(lng), Toast.LENGTH_SHORT).show();
                        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        // Add the hash and the lat/lng to the document. We will use the hash
                        // for queries and the lat/lng for distance comparisons.
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("geohash", hash);
                        updates.put("lat", lat);
                        updates.put("lng", lng);
                        updates.put("accuracy", location.getAccuracy());
                        updates.put("date", t1.getCurrentDate());
                        updates.put("time", t1.getCurrentTime());
                        updates.put("Device Speed", location.getSpeed());
                        updates.put("BatteryStatus", level);
                        db.collection("Users").document(t1.currentUserid()).update(updates);
                        //Toast.makeText(mContext, "Updating Location..", Toast.LENGTH_SHORT).show();
                        dangerStatus();
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }



    private void updateFirestore(Location location) {
        // Create a new document with the location data

    }

    //its uploads data to firebase database!! for Danger status by sending random values
    void dangerStatus()
    {
        double garbage  = Math.random();
        // Write a message to the database
        Map<String, String> m1 = new HashMap<>();
        m1.put("DANGERMODE",String.valueOf(garbage));
        m1.put("USERID",t1.currentUserid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UsersStatusData").child(currentuserid());
        myRef.setValue(m1);
    }

    String currentuserid()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authuserid = user.getUid();
        }
        return authuserid;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(mContext, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
}