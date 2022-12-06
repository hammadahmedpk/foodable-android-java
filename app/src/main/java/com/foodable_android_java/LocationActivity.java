package com.foodable_android_java;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.foodable_android_java.databinding.ActivityLocationBinding;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.IOException;
import java.util.List;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityLocationBinding binding;
    EditText searchText;
    private FusedLocationProviderClient fusedLocationClient;
    AppCompatButton confirmLocation;
    LatLng currentLocation;
    ImageButton searchButton, myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        confirmLocation = findViewById(R.id.confirm_location_button);
        searchText = findViewById(R.id.search_location);
        searchButton = findViewById(R.id.search_button);
        myLocation = findViewById(R.id.my_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = searchText.getText().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(LocationActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    currentLocation = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                    searchText.setText("");
                }
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                        .setGranularity(Granularity.GRANULARITY_FINE)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setDurationMillis(2000)
                        .setMaxUpdateAgeMillis(0)
                        .build();

                    CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                    fusedLocationClient.getCurrentLocation(request, cancellationTokenSource.getToken())
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                            }
                            else {
                                Toast.makeText(LocationActivity.this, "Location not found\nTry Again!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {}
                        );
                }
                else {
                    Toast.makeText(LocationActivity.this, "Location Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("latitude", currentLocation.latitude);
                intent.putExtra("longitude", currentLocation.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Last Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    }
                });
        }
        else {
            currentLocation = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                currentLocation = new LatLng(point.latitude, point.longitude);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("New Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
            }
        });

    }

}