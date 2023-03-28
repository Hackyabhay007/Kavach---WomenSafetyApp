package com.hackydesk.kavach_womensafetyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;


import com.google.common.util.concurrent.ListenableFuture;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class PoliceMode extends AppCompatActivity    {

    private static final int REQUEST_CAMERA_PERMISSION = 201;


    ArrayList<String> permissionsList;
    AlertDialog alertDialog;
    String[] permissionsStr = {Manifest.permission.CAMERA,android.Manifest.permission.CALL_PHONE};
    int permissionsCount = 0;

    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                } else if (!hasPermission(getApplicationContext(), permissionsStr[i])) {
                                    permissionsCount++;
                                }
                            }
                            if (permissionsList.size() > 0) {
                                //Some permissions are denied and can be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
                                showPermissionDialog();
                                askForPermissions(permissionsList);
                            } else {
                                //All permissions granted. Do your stuff 🤞
                                //  addresstext.setText("All permissions are granted!");
                                // Toast.makeText(getApplicationContext(), "All Permissions Granted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_mode);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        //ask for permissions
        askForPermissions(permissionsList);



    }

    private void askForPermissions(ArrayList<String> permissionsList) {

        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);


        } else {
            // showPermissionDialog();
        }

        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(PoliceMode.this);
            builder1.setTitle("Location Permission Needed");
            builder1.setMessage("Location will be Used to Sync location with server in background only  when SOS MODE OR SHARE JOURNEY MODE are at running State. You Can Stop Them Easily By Stop Button in Home Section,  guardian can get your location and other device details when You allow them. location will be used for SOS feature ,Review Area feature  and SHARE JOURNEY  feature only ");
            builder1.setIcon(R.drawable.female_avatar_girl_face_woman_user_2_svgrepo_com);
            builder1.create();

            builder1.setPositiveButton("Allow", (dialog, which) -> {
                ActivityCompat.requestPermissions(PoliceMode.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                dialog.cancel();
            }).setNegativeButton("Close ", (dialog, which) -> Toast.makeText(PoliceMode.this, "Permission Denied", Toast.LENGTH_SHORT).show());
            AlertDialog alert2 = builder1.create();
            alert2.show();
        }
    }
    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionDialog() {
        //addresstext.setText("Showing settings dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert_Bridge);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are needed to be allowed to use this app without any problems.").setCancelable(false)
                .setPositiveButton("Allow", (ans, yes) -> {
                    permissionsList = new ArrayList<>();
                    permissionsList.addAll(Arrays.asList(permissionsStr));
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        }
    }


}