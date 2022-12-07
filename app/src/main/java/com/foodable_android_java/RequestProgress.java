package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class RequestProgress extends AppCompatActivity {
    TextView donationName, donationDesc, receiverName, receiverDesc, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_progress);
        donationName = findViewById(R.id.donorName1);
        donationDesc = findViewById(R.id.donorDesc1);
        receiverName = findViewById(R.id.receiverName1);
        receiverDesc = findViewById(R.id.recvDesc1);
        distance = findViewById(R.id.distance1);
        donationName.setText(getIntent().getStringExtra("donationName"));
        donationDesc.setText(getIntent().getStringExtra("donationNDesc"));
        receiverName.setText(getIntent().getStringExtra("receiverName"));
        receiverDesc.setText(getIntent().getStringExtra("receiverDesc"));
        String dist = String.valueOf(SphericalUtil.computeDistanceBetween(new LatLng(Double.parseDouble(getIntent().getStringExtra("donationLocationLat")), Double.parseDouble(getIntent().getStringExtra("donationLocationLng"))), new LatLng(Double.parseDouble(getIntent().getStringExtra("receiverLocationLat")), Double.parseDouble(getIntent().getStringExtra("receiverLocationLng")))));
        distance.setText(dist);
    }
}