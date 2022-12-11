package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private static final String ONESIGNAL_APP_ID = "b717cf86-240c-4897-a95a-1a39996c2e23";
    String donationName, donationLocationLat, donationLocationLng, donationDesc, receiverName, receiverDesc, receiverLocationLat, receiverLocationLng;
    double latitude = 0.0, longitude = 0.0;

    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult result) {
                try {
                    flag = true;
                    donationName = result.getNotification().getAdditionalData().getString("donationName");
                    donationLocationLat = result.getNotification().getAdditionalData().getString("donationLocationLat");
                    donationLocationLng = result.getNotification().getAdditionalData().getString("donationLocationLng");
                    donationDesc = result.getNotification().getAdditionalData().getString("donationDesc");
                    receiverName = result.getNotification().getAdditionalData().getString("receiverName");
                    receiverDesc = result.getNotification().getAdditionalData().getString("receiverDesc");
                    receiverLocationLat = result.getNotification().getAdditionalData().getString("receiverLocationLat");
                    receiverLocationLng = result.getNotification().getAdditionalData().getString("receiverLocationLng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(MainActivity.this, RequestProgress.class);
                intent.putExtra("donationName", donationName);
                intent.putExtra("donationLocationLat", donationLocationLat);
                intent.putExtra("donationLocationLng", donationLocationLng);
                intent.putExtra("donationDesc", donationDesc);
                intent.putExtra("receiverName", receiverName);
                intent.putExtra("receiverDesc", receiverDesc);
                intent.putExtra("receiverLocationLat", receiverLocationLat);
                intent.putExtra("receiverLocationLng", receiverLocationLng);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        // For slider animation
        ImageView image = findViewById(R.id.splashLogo);
        TextView title = findViewById(R.id.title);
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen);
        image.startAnimation(slideAnimation);
        title.startAnimation(slideAnimation);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        setUserLocation();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(flag == false)
                {
                    if(user != null) {
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        SharedPreferences locationSP = getSharedPreferences("location",MODE_PRIVATE);
                        SharedPreferences.Editor editor = locationSP.edit();
                        editor.putString("latitude", String.valueOf(latitude));
                        editor.putString("longitude", String.valueOf(longitude));
                        editor.apply();
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(MainActivity.this, Signup.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 5000L);
    }

    private void setUserLocation(){
        FusedLocationProviderClient fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                    .setGranularity(Granularity.GRANULARITY_FINE)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setDurationMillis(4500)
                    .setMaxUpdateAgeMillis(0)
                    .build();

            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(request, cancellationTokenSource.getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            Toast.makeText(MainActivity.this, "Location Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

}