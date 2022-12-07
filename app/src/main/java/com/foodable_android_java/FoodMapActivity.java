package com.foodable_android_java;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;


public class FoodMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    LatLng currentLocation;
    ImageButton backButtton, myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodmap);
        myLocation = findViewById(R.id.my_location);
        backButtton = findViewById(R.id.back_button);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backButtton.setOnClickListener(v -> {
            finish();
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(FoodMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FoodMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                                    getOtherDonations();
                                }
                                else {
                                    Toast.makeText(FoodMapActivity.this, "Location not found\nTry Again!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {}
                            );
                }
                else {
                    Toast.makeText(FoodMapActivity.this, "Location Permission not granted", Toast.LENGTH_SHORT).show();
                }
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
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                getOtherDonations();
            }
        });

        getOtherDonations();

    }

    public void getOtherDonations() {
        FirebaseDatabase.getInstance().getReference("donations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String lat = snapshot1.child("location").child("latitude").getValue().toString();
                        String lng = snapshot1.child("location").child("longitude").getValue().toString();
                        LatLng donationLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        if (SphericalUtil.computeDistanceBetween(currentLocation, donationLocation) < 5000){
                            mMap.addMarker(new MarkerOptions().position(donationLocation).title("Donation Here"));
                        }
                    }
                }
            }
        });
    }

}