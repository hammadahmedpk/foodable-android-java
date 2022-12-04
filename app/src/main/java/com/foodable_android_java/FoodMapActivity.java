package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

public class FoodMapActivity extends AppCompatActivity {
    ImageButton backButtton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodmap);
        backButtton = findViewById(R.id.back_button);
        backButtton.setOnClickListener(v -> {
            finish();
        });
    }
}