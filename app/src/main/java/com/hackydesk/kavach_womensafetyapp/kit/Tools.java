package com.hackydesk.kavach_womensafetyapp.kit;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Tools {

    Context context;
    Activity activity;

    public Tools(Activity activity) {
        this.activity = activity;
    }
    public Tools(Context context) {
        this.context = context;
    }
    public Tools(Activity activity,Context context) {
        this.context = context;
        this.activity = activity;
    }

    public void savetoDb(String key, String value) {
        SharedPreferences preferences = activity.getSharedPreferences("KAVACH_DATABASE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getData(String key)
    {
        SharedPreferences preferences = activity.getSharedPreferences("KAVACH_DATABASE", Context.MODE_PRIVATE);
        return  preferences.getString(key,"null");
    }


    public void sentT0Firestore(String collectionpath,HashMap<String,String> data)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionpath).document((Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               logSender(true);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logSender(false);
            }
        });
    }

    public boolean logSender(boolean val)
    {
        return val;
    }

    public String currentUserid()
    {
        String authuserid="";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authuserid = user.getUid();
        }
        return authuserid;
    }

    public String getCurrentDate()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currentdate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = Currentdate.format(new Date());
        return  currentDate;
    }

    public String getCurrentTime()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currenttime = new SimpleDateFormat("hh:mm:ss a");
        String currentTime = Currenttime.format(new Date());
        return  currentTime;
    }

    public String getCurrentTimeWithoutAMPM()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat Currenttime = new SimpleDateFormat("hh:mm:ss");
        String currentTime = Currenttime.format(new Date());
        return  currentTime;
    }

    public boolean isServiceRunning(Class<?> serviceClass,Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
