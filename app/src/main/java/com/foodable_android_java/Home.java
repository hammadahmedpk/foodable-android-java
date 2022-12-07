package com.foodable_android_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    TextView userName;
    ImageView profile;
    DrawerLayout drawerLayout;
    ImageView toggler;
    RecyclerView rv;
    FoodCardAdapter adapter;
    List<FoodCardModel> foodCard;
    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // remove the status bar blue color
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        drawerLayout= findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View HeaderView = navigationView.getHeaderView(0);
        userName = HeaderView.findViewById(R.id.userName);
        profile = HeaderView.findViewById(R.id.profileImage);
        toggler= findViewById(R.id.toggler);

        SharedPreferences sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
        String Fname = sharedPreferences.getString("firstName", "");
        String lName= sharedPreferences.getString("lastName", "");
        userName.setText(Fname + " " + lName);
        String Url= sharedPreferences.getString("profile", "");

        // set the profile image
        Glide.with(getApplicationContext()).load(Url).circleCrop().into(profile);



        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationHandle();

        toggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        rv= findViewById(R.id.rv);
        foodCard = new ArrayList<>();
        adapter = new FoodCardAdapter(foodCard, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Set User location
        setUserLocation();

    }




    private void setUserLocation(){
        FusedLocationProviderClient fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            // get all the data from the database
                            FirebaseDatabase.getInstance().getReference("donations").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DataSnapshot dataSnapshot = task.getResult();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                String title= snapshot1.child("title").getValue().toString();
                                                String lat = snapshot1.child("location").child("latitude").getValue().toString();
                                                String lng = snapshot1.child("location").child("longitude").getValue().toString();
                                                LatLng donationLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                                String userName= snapshot1.child("name").getValue().toString();
                                                String userProfile= snapshot1.child("profile").getValue().toString();
                                                double distance= SphericalUtil.computeDistanceBetween(currentLocation, donationLocation);
                                                String distanceStr= String.valueOf(distance);
                                                if(distance>1000) {
                                                    // round to two decimal places
                                                    distanceStr= String.format("%.2f", distance/1000)+ " km";

                                                }else{
                                                    distanceStr= String.valueOf(distance)+" m";
                                                }
                                                String ItemImage= snapshot1.child("images").child("0").getValue().toString();
                                                foodCard.add(new FoodCardModel(ItemImage, title, distanceStr, userName, userProfile));
                                            }
                                        }
                                        adapter.setList(foodCard);
                                    }
                                });


                            // toast the location
                            Toast.makeText(Home.this, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Home.this, "Location not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Home.this, "Location not found!33", Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            Toast.makeText(Home.this, "Location Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }




    private void bottomNavigationHandle() {
        // add event listener to bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // log to console
            System.out.println(item.getItemId());
            System.out.println(item.getItemId());


            if (item.getItemId() == R.id.map) {
                Intent intent = new Intent(getApplicationContext(), FoodMapActivity.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.goal){
                Intent intent = new Intent(getApplicationContext(), DonateActivity.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.setting){
                Intent intent = new Intent(getApplicationContext(), ReceiveActivity.class);
                startActivity(intent);
            }
            return true;
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.edit_profile:{
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
                break;
            }
            case R.id.history:{
                Intent intent = new Intent(getApplicationContext(), History.class);
                startActivity(intent);
                break;
            }

            case R.id.logout: {
                Log.d("logout", "onClick: logout");
                SharedPreferences preferences = getSharedPreferences("userProfile",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                // logout from firebase
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Home.this, Signin.class);
                // clear all activities stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}