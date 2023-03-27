package com.hackydesk.kavach_womensafetyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackydesk.kavach_womensafetyapp.kit.BackgroundListener;
import com.hackydesk.kavach_womensafetyapp.kit.DangerModeService;
import com.hackydesk.kavach_womensafetyapp.kit.Tools;


public class Main_home extends Fragment implements OnMapReadyCallback {

    TextView sosmode,callpolice;
    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationRequest locationRequest;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GoogleMap Mymap;
    private Tools tools = new Tools(getActivity(),getContext());
    FrameLayout sos_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_home, container,false);

        sosmode = rootView.findViewById(R.id.SOS);
        callpolice = rootView.findViewById(R.id.callPolice);
        sos_btn = rootView.findViewById(R.id.sos_btn);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check for active services
        checkForActive();

        sosmode.setOnClickListener(v -> {
            boolean running =  tools.isServiceRunning(DangerModeService.class,getActivity());
            if (running)
            {
                System.exit(0);
            }
            Intent serviceIntent = new Intent(getContext(),DangerModeService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(serviceIntent);
            }
            dangerbtnanimation();

            Toast.makeText(getContext(), "SOS MODE ENABLED", Toast.LENGTH_SHORT).show();
        });
        callpolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "calling nearest police station", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
    void dangerbtnanimation() {

        sosmode.setText("STOP");
        sosmode.post(new Runnable() {
            int i = 0;
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void run() {
                sos_btn.setAlpha((float) 0.7);

                i++;
                if (i == 2) {
                    i = 0;
                    sos_btn.setAlpha((float) 0.9);
                }
                sosmode.postDelayed(this, 1000);
                sosmode.setTextColor(Color.RED);
            }});

    }

    void checkForActive()
    {
       boolean running =  tools.isServiceRunning(DangerModeService.class,getActivity());

       if (running)
       {
           dangerbtnanimation();
       }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Mymap  = googleMap;
        getCurrentLocation();
    }

    void getCurrentLocation()
    {
        Context context = getContext();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//Not the best practices to get runtime permissions, but still here I ask permissions.
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
//Instantiating the Location request and setting the priority and the interval I need to update the location.
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//instantiating the LocationCallBack
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                //Showing the latitude, longitude and accuracy on the home screen.
                for (Location location : locationResult.getLocations()) {
                   Double lat    = location.getLatitude();
                    Double lng   = location.getLongitude();
                 //   Toast.makeText(context, String.valueOf(lat), Toast.LENGTH_SHORT).show();
                    mapUpdater(lat,lng);
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    void mapUpdater(double lat , double lng) {
        LatLng position = new LatLng(lat, lng);
        Mymap.clear();
        Marker m1 =  Mymap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title("User Position")
                      );

        assert m1 != null;
        m1.setPosition(position);
        Mymap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,16f));
    }

}