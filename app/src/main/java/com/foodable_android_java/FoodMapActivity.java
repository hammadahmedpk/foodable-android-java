package com.foodable_android_java;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;


public class FoodMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    LatLng currentLocation;
    ImageButton backButtton, myLocation;
    Marker currentMarker;
    ArrayList<Donation> donations;
    ArrayList<String> donorIds;
    ArrayList<String> donationIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodmap);
        myLocation = findViewById(R.id.my_location);
        backButtton = findViewById(R.id.back_button);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        donations = new ArrayList<>();
        donorIds = new ArrayList<>();
        donationIds = new ArrayList<>();
        getAllDonations();

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
                            .setDurationMillis(4000)
                            .setMaxUpdateAgeMillis(0)
                            .build();

                    CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                    fusedLocationClient.getCurrentLocation(request, cancellationTokenSource.getToken())
                            .addOnSuccessListener(location -> {
                                if (location != null) {
                                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.clear();
                                    currentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
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
                        currentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Last Location"));
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
                currentMarker =  mMap.addMarker(new MarkerOptions().position(currentLocation).title("New Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                getOtherDonations();
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                getOtherDonations();
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (!marker.equals(currentMarker)) {
                    View v = getLayoutInflater().inflate(R.layout.marker_infowindow, null);
                    TextView userName = v.findViewById(R.id.userName);
                    TextView itemName = v.findViewById(R.id.itemName);
                    ImageView itemImg = v.findViewById(R.id.itemImg);

                    for (Donation donation : donations) {
                        if (donation.getLocation().equals(marker.getPosition())) {
                            userName.setText(donation.getName());
                            itemName.setText(donation.getTitle());
                            double dist = SphericalUtil.computeDistanceBetween(donation.getLocation(), marker.getPosition());
                            String distanceStr = String.valueOf(dist);
                            distanceStr = String.format("%.2f", dist / 1000) + " km";
                            Glide.with(FoodMapActivity.this).load(donation.getImages().get(0)).into(itemImg);
                            break;
                        }
                    }
                    return v;
                }
                else {
                    return null;
                }

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!marker.equals(currentMarker)) {
                    Intent intent = new Intent(FoodMapActivity.this, ReceiveActivity.class);
                    for (Donation donation : donations) {
                        if (donation.getLocation().equals(marker.getPosition())) {
                            intent.putExtra("donorId", donorIds.get(donations.indexOf(donation)));
                            intent.putExtra("donationId", donationIds.get(donations.indexOf(donation)));
                            intent.putExtra("donationName", donation.getName());
                            intent.putExtra("donationDesc", donation.getDescription());
                            intent.putExtra("donationLocationLat", String.valueOf(donation.getLocation().latitude));
                            intent.putExtra("donationLocationLng", String.valueOf(donation.getLocation().longitude));
                            intent.putExtra("donationImg", donation.getImages().get(0));
                            break;
                        }
                    }
                    startActivity(intent);
                }
            }
        });

    }

    public void getOtherDonations() {
        FirebaseDatabase.getInstance().getReference("donations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String lat = snapshot1.child("location").child("latitude").getValue().toString();
                            String lng = snapshot1.child("location").child("longitude").getValue().toString();
                            LatLng donationLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            if (SphericalUtil.computeDistanceBetween(currentLocation, donationLocation) < 5000) {
                                mMap.addMarker(new MarkerOptions().position(donationLocation).title("Donation Here"));
                            }
                        }
                    }
                }
            }
        });
    }

    public void getAllDonations(){
        FirebaseDatabase.getInstance().getReference("donations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        String donorId = snapshot.getKey();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Donation donation = new Donation();
                            donation.setTitle(snapshot1.child("title").getValue().toString());
                            donation.setName(snapshot1.child("name").getValue().toString());
                            donation.setDescription(snapshot1.child("description").getValue().toString());
                            donation.setProfile(snapshot1.child("profile").getValue().toString());
                            ArrayList<String> images = new ArrayList<>();
                            images.add(snapshot1.child("images").child("0").getValue().toString());
                            donation.setImages(images);
                            donation.setLocation(new LatLng(Double.parseDouble(snapshot1.child("location").child("latitude").getValue().toString()), Double.parseDouble(snapshot1.child("location").child("longitude").getValue().toString())));
                            donations.add(donation);
                            donorIds.add(donorId);
                            donationIds.add(snapshot1.getKey());
                        }
                    }
                }
            }
        });
    }

}