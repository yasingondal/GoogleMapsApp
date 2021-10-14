package com.example.mygooglemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    public static final int FAST_UPDATE_INTERVAL = 30;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;


    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationupdates, sw_gps;

    //Google api for Location Services...
    FusedLocationProviderClient fusedLocationProviderClient;

    //Config File for to apply diff setting to fusedlocationproviderclient.
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XmlInitializations();
        RequestMapPermissions();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //For getting Location
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        //As soon as location will be available it will be collected.
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        //For Setting Accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sw_gps.setOnClickListener(view -> {
            if (sw_gps.isChecked()) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                tv_sensor.setText("Using GPS Sensors");
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                tv_sensor.setText("Using Towers + Wifi");
            }

        });

        sw_locationupdates.setOnClickListener(view -> {
            if(sw_locationupdates.isChecked()){
                //Track Location each 3 sec...
                StartLocationUpdate();
            }else{
                //Stop Tracking...
                StopLocationUpdate();
            }
        });

    }

    private void StopLocationUpdate() {
        tv_address.setText("");
        tv_speed.setText("");
        tv_altitude.setText("");
        tv_accuracy.setText("");
        tv_lon.setText("");
        tv_lat.setText("");
        tv_sensor.setText("");
    }

    private void StartLocationUpdate() {

    }

    private void XmlInitializations() {
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_updates = findViewById(R.id.tv_updates);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_address = findViewById(R.id.tv_address);

        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);
    }

    // All The Requests for Permissions..
    private void RequestMapPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
    }


    //Handling Permissions Reponses here..
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                    //Continue if have permissions...
                    updateGPS();
                } else {
                    Toast.makeText(MainActivity.this, "Please Turn On Location First", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    finish();
                }

                return;
            }
        }
    }


    private void updateGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Do all the code for lat,lon etc....
                    UpdateUIValues(location);
                }
            });
        }

    } //Update GPS

    private void UpdateUIValues(Location location) {
        //Update all of the TextViews with updated Location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        //Ability to check Altitude and Speed vary from mobile to mobile if mobile will be low standard it cant calculate..
        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }else{
            tv_altitude.setText("Altitude Not Available");
        }

        //For Altitude
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }else{
            tv_speed.setText("Speed Not Available");
        }


    }


} //AppCompact