package com.foodable_android_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    TextView userName;
    ImageView profile;
    DrawerLayout drawerLayout;
    ImageView toggler;

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
        bottomNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        toggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


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