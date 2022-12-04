package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // remove the status bar blue color
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

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