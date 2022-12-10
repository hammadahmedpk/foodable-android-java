package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class RequestProgress extends AppCompatActivity {
    TextView donationName, donationDesc, receiverName, receiverDesc, distance;
    String donationLocationLat, donationLocationLng, receiverLocationLat, receiverLocationLng;
    AppCompatButton requestCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_progress);
        donationName = findViewById(R.id.donorName1);
        donationDesc = findViewById(R.id.donorDesc1);
        receiverName = findViewById(R.id.receiverName1);
        receiverDesc = findViewById(R.id.recvDesc1);
        distance = findViewById(R.id.distance1);
        requestCompleted = findViewById(R.id.requestCompleted);
        donationName.setText(getIntent().getStringExtra("donationName"));
        donationDesc.setText(getIntent().getStringExtra("donationDesc"));
        receiverName.setText(getIntent().getStringExtra("receiv" +
                "erName"));
        receiverDesc.setText(getIntent().getStringExtra("receiverDesc"));

        donationLocationLat = getIntent().getStringExtra("donationLocationLat");
        donationLocationLng = getIntent().getStringExtra("donationLocationLng");
        receiverLocationLat = getIntent().getStringExtra("receiverLocationLat");
        receiverLocationLng = getIntent().getStringExtra("receiverLocationLng");

        System.out.println("Donation Location Lat: " + donationLocationLat);
        System.out.println("Donation Location Lng: " + donationLocationLng);
        System.out.println("Receiver Location Lat: " + receiverLocationLat);
        System.out.println("Receiver Location Lng: " + receiverLocationLng);


        LatLng donationLocation = new LatLng(Double.parseDouble(donationLocationLat), Double.parseDouble(donationLocationLng));
        LatLng receiverLocation = new LatLng(Double.parseDouble(receiverLocationLat), Double.parseDouble(receiverLocationLng));


        double dist = SphericalUtil.computeDistanceBetween(donationLocation, receiverLocation);

        String distanceStr = String.format("%.2f", dist/1000)+ " km";
        distance.setText(distanceStr);

        requestCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestProgress.this, Home.class));
            }
        });
    }
}