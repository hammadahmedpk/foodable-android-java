package com.foodable_android_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ReceiveActivity extends AppCompatActivity {
    ImageButton backButtton;
    ImageView selectLocation;
    EditText name, description;
    AppCompatButton submitButton;
    Double latitude = 0.0, longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        selectLocation = findViewById(R.id.select_location);
        name = findViewById(R.id.name);
        description = findViewById(R.id.desc);
        submitButton = findViewById(R.id.submit_button);
        backButtton = findViewById(R.id.back_button);

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
                    Toast.makeText(ReceiveActivity.this, "Data Validated!", Toast.LENGTH_SHORT).show();
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

}