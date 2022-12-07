package com.foodable_android_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReceiveActivity extends AppCompatActivity {
    ImageButton backButtton;
    ImageView selectLocation;
    EditText name, description;
    AppCompatButton submitButton;
    Double latitude = 0.0, longitude = 0.0;
    String donationId, donationName, donationDesc, donationLocationLat, donationLocationLng, donationImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        selectLocation = findViewById(R.id.select_location);
        name = findViewById(R.id.name);
        description = findViewById(R.id.desc);
        submitButton = findViewById(R.id.submit_button);
        backButtton = findViewById(R.id.back_button);
        donationId = getIntent().getStringExtra("donationId");
        donationName = getIntent().getStringExtra("donationName");
        donationLocationLat = getIntent().getStringExtra("donationLocationLat");
        donationLocationLng = getIntent().getStringExtra("donationLocationLng");
        donationDesc = getIntent().getStringExtra("donationDesc");
        donationImg = getIntent().getStringExtra("donationImg");

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiveActivity.this, LocationActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        backButtton.setOnClickListener(v -> {
            finish();
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty() || description.getText().toString().isEmpty() || latitude == 0.0 || longitude == 0.0) {
                    Toast.makeText(ReceiveActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReceiveActivity.this, "Receiver Information Uploaded", Toast.LENGTH_SHORT).show();
                    sendNotification();
                    Intent i = new Intent(ReceiveActivity.this, RequestProgress.class);
                    i.putExtra("donationName", donationName);
                    i.putExtra("donationLocationLat", donationLocationLat);
                    i.putExtra("donationLocationLng", donationLocationLng);
                    i.putExtra("donationDesc", donationDesc);
                    i.putExtra("receiverName", name.getText().toString());
                    i.putExtra("receiverDesc", description.getText().toString());
                    i.putExtra("receiverLocationLat", latitude.toString());
                    i.putExtra("receiverLocationLng", longitude.toString());
                    startActivity(i);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            if (latitude != 0.0 && longitude != 0.0) {
                Toast.makeText(this, "Location is Set", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Unable to get Location", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void sendNotification(){
        SharedPreferences sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
        String userName = sharedPreferences.getString("firstName", "") + " " + sharedPreferences.getString("lastName", "");
        JSONObject json = null;
        try {
            json= new JSONObject("{'app_id':'b717cf86-240c-4897-a95a-1a39996c2e23'," +
                    "'include_external_user_ids': [ '" + donationId + "' ]," +
                    "'contents': { 'en' : 'Your Item is Requested' } ," +
                    " 'headings' :{'en':'Message From "+userName+"'} }");
            json.put("large_icon", donationImg);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ReceiveActivity.this,"JSON Error",Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://onesignal.com/api/v1/notifications", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //now handle the response
                Toast.makeText(ReceiveActivity.this,  "Notification Sent", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle the error
                Toast.makeText(ReceiveActivity.this, "Notification Not Sent", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        })
        {    //adding header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic MWExMTYyYzUtNzNhMy00ZjBjLWEzNTAtN2IwMWU0MDlhYjBi");
                params.put("Content-type", "application/json");
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(ReceiveActivity.this);
        queue.add(jsonRequest);
    }

}