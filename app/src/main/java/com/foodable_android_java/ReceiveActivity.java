package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class ReceiveActivity extends AppCompatActivity {

    ImageButton backButtton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        backButtton = findViewById(R.id.back_button);
        backButtton.setOnClickListener(v -> {
            finish();
        });
    }
}