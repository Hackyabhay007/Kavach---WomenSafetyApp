package com.hackydesk.kavach_womensafetyapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hackydesk.kavach_womensafetyapp.kit.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PoliceMode extends AppCompatActivity  {
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    String userId;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction,Last_User;
    String intentData = "";

    boolean isEmail = false;

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

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        Last_User = findViewById(R.id.last_user);
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        //ask for permissions
        askForPermissions(permissionsList);
        initViews();


    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adddata(txtBarcodeValue.getText().toString());
                Toast.makeText(PoliceMode.this, "User Added", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(PoliceMode.this, police_dashboard.class);
                startActivity(intent2);
            }
        });

        Last_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            checkLast();
            }
        });
            if (intentData.length() > 0) {
                if (isEmail)
                    startActivity(new Intent(PoliceMode.this, EmailActivity.class).putExtra("email_address", intentData));
                else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                }
                }
            }


    private void initialiseDetectorsAndSources() {

      //  Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(PoliceMode.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(PoliceMode.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
               // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                                btnAction.setText("EMAIL");
                            } else {
                                isEmail = false;
                                btnAction.setText("FIND USER");
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);
                            }
                        }
                    });
                }
            }
        });
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
          //  builder1.setMessage("Location will be Used to Sync location with server in background only  when SOS MODE OR SHARE JOURNEY MODE are at running State. You Can Stop Them Easily By Stop Button in Home Section,  guardian can get your location and other device details when You allow them. location will be used for SOS feature ,Review Area feature  and SHARE JOURNEY  feature only ");
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
    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public void adddata(String USER_ID)
    {
        Tools t1 = new Tools(PoliceMode.this,getApplicationContext());
        t1.savetoDb("useridForPolice",USER_ID);
    }
    public void checkLast()
    {
        Tools t1 = new Tools(PoliceMode.this , getApplicationContext());
        String checkForUser =  t1.getData("useridForPolice");
        if (!checkForUser.contains("null"))
        {
            Intent intent = new Intent(PoliceMode.this,police_dashboard.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(PoliceMode.this, "No User Found", Toast.LENGTH_SHORT).show();
        }
    }


}