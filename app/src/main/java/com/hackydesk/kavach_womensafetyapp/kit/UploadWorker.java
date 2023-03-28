package com.hackydesk.kavach_womensafetyapp.kit;


import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackydesk.kavach_womensafetyapp.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class UploadWorker extends Worker {

    Context mContext;
    String  authuserid;
    Tools t1;
    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationRequest locationRequest;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotificationManager notificationManager;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
        t1 = new Tools(context);
    }

    @Override
    public Result doWork() {
        Data taskData = getInputData();
        String taskDataString = taskData.getString("Dw");
        showNotification("WorkManager", taskDataString != null ? taskDataString : "Message has been Sent");
        Data outputData = new Data.Builder().putString("WORK_RESULT", "Jobs Finished").build();
        deviceStatus();
        return Result.success(outputData);
    }
    private Result showNotification(String task, String desc) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_channel";
        String channelName = "task_name";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(1, builder.build());
        return Result.success();
    }


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
    void dangerStatus()
    {
        double garbage  = Math.random();
        // Write a message to the database
        Map<String, String> m1 = new HashMap<>();
        m1.put("DANGERMODE",String.valueOf(garbage));
        m1.put("USERID",t1.currentUserid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UsersStatusData").child(t1.currentUserid());
        myRef.setValue(m1);
    }

}