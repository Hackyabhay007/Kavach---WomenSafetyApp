package com.hackydesk.kavach_womensafetyapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hackydesk.kavach_womensafetyapp.kit.BackgroundListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Home extends AppCompatActivity {

    ArrayList<String> permissionsList;
    String[] permissionsStr = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CALL_PHONE, android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.RECORD_AUDIO};
    int permissionsCount = 0;
    AlertDialog alertDialog;
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;



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

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("KAVATCH - Safety Kit");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //starts background service
        startBackgroundListener();
        //shek detector


        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        //ask for permissions
        askForPermissions(permissionsList);



        bottomNavigationView =findViewById(R.id.bottom_navigation);

        Fragment main_home = new Main_home();
        Fragment settings = new Setting();
        Fragment emergencydailer = new Dialer();
        Fragment profile = new Profile();
        fragloader(main_home, 0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // do stuff
            switch (item.getItemId()) {
                case R.id.home:
                    fragloader(main_home, 1);
                    return true;
                case R.id.dialer:
                    fragloader(emergencydailer, 1);
                    return true;
                case R.id.profile:
                    fragloader(profile, 1);
                    return true;
                case R.id.setting:
                    fragloader(settings,0);
                    return true;

            }
            return false;
        });
    }


    void  fragloader(Fragment fragment , int flag)
    {
        FragmentManager fm =getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag==0)
            ft.replace(R.id.fragmentContainerView,fragment).commit();
        else
            ft.replace(R.id.fragmentContainerView,fragment).commit();
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
            builder1.setTitle("Location Permission Needed");
            builder1.setMessage("Location will be Used to Sync location with server in background only  when SOS MODE OR SHARE JOURNEY MODE are at running State. You Can Stop Them Easily By Stop Button in Home Section,  guardian can get your location and other device details when You allow them. location will be used for SOS feature ,Review Area feature  and SHARE JOURNEY  feature only ");
            builder1.setIcon(R.drawable.female_avatar_girl_face_woman_user_2_svgrepo_com);
            builder1.create();


            builder1.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    dialog.cancel();
                }
            }).setNegativeButton("Close ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Home.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

            });

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

    private void startBackgroundListener() {
        Intent serviceIntent = new Intent(getApplicationContext(),BackgroundListener.class);
        startService(serviceIntent);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       stopService(new Intent(getApplicationContext(), BackgroundListener.class));
    }

}