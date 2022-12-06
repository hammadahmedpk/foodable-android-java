package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    TextView userName;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // remove the status bar blue color
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        navigationView = findViewById(R.id.navigation_view);
        View HeaderView = navigationView.getHeaderView(0);
        userName = HeaderView.findViewById(R.id.userName);
        profile = HeaderView.findViewById(R.id.profileImage);

        SharedPreferences sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
        String Fname = sharedPreferences.getString("firstName", "");
        String lName= sharedPreferences.getString("lastName", "");
        userName.setText(Fname + " " + lName);
        String Url= sharedPreferences.getString("profile", "");
        profile.setImageURI(getIntent().getData());



        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationHandle();
        bottomNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
}