package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

public class DonateActivity extends AppCompatActivity {
    ImageButton backButtton;
    Spinner list_days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        Spinner dropdownDays = findViewById(R.id.list_days);
        String[] days = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        dropdownDays.setAdapter(adapter);
        backButtton = findViewById(R.id.back_button);
        backButtton.setOnClickListener(v -> {
            finish();
        });
    }
}