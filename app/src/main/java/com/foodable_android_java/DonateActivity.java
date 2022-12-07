package com.foodable_android_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DonateActivity extends AppCompatActivity {
    ImageButton backButtton;
    AppCompatButton submitButton, quantity1, quantity2, quantity3, quantity4, quantity5;
    Spinner list_days;
    ImageView selectImage, selectLocation;
    EditText title, description, pickUpTime, otherQuantity;
    Integer quantity = 0;
    Double latitude = 0.0, longitude = 0.0;
    Boolean isImageSelected = false;
    ArrayList<Uri> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        backButtton = findViewById(R.id.back_button);
        selectImage = findViewById(R.id.select_image);
        selectLocation = findViewById(R.id.select_location);
        title = findViewById(R.id.title);
        description = findViewById(R.id.desc);
        otherQuantity = findViewById(R.id.other_quantity);
        pickUpTime = findViewById(R.id.pickuptime);
        list_days = findViewById(R.id.list_days);
        submitButton = findViewById(R.id.submit_button);

        String[] days = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        list_days.setAdapter(adapter);

        checkQuantity();

        backButtton.setOnClickListener(v -> {
            finish();
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(i, "Choose Images"), 100);
            }
        });

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonateActivity.this, LocationActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().isEmpty() || description.getText().toString().isEmpty() || pickUpTime.getText().toString().isEmpty() || quantity == 0 || latitude == 0.0 || longitude == 0.0 || isImageSelected == false) {
                    Toast.makeText(DonateActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                } else
                {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("donations").child(FirebaseAuth.getInstance().getUid());
                    String key = dbRef.push().getKey();
                    dbRef = dbRef.child(key);
                    ProgressDialog progressDialog = new ProgressDialog(DonateActivity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    SharedPreferences sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
                    String name = sharedPreferences.getString("firstName", "") + " " + sharedPreferences.getString("lastName", "");
                    String profile = sharedPreferences.getString("profile", "");
                    Donation donation = new Donation(name, profile, title.getText().toString(), description.getText().toString(), pickUpTime.getText().toString(), quantity.toString(), list_days.getSelectedItem().toString(), new LatLng(latitude, longitude));
                    dbRef.setValue(donation);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("donationImgs");

                    for (int i = 0; i < images.size(); i++) {
                        StorageReference imgRef = storageReference.child(key + "_" + i);
                        DatabaseReference dbImgRef = dbRef.child("images").child(String.valueOf(i));
                        int finalI = i;
                        imgRef.putFile(images.get(i)).addOnCompleteListener(taskSnapshot -> {
                            imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                dbImgRef.setValue(uri.toString());
                                if (finalI == images.size() - 1) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    Toast.makeText(DonateActivity.this, "Donation Added", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            });
                        });

                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    images.add(imageUri);
                    selectImage.setImageURI(imageUri);
                    isImageSelected = true;

                }
            }
            else {
                Uri imageUri = data.getData();
                images.add(imageUri);
                selectImage.setImageURI(imageUri);
                isImageSelected = true;

            }

        }

        else if (requestCode == 1 && resultCode == RESULT_OK) {
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

    private void checkQuantity(){
        quantity1 = findViewById(R.id.quantity_1);
        quantity2 = findViewById(R.id.quantity_2);
        quantity3 = findViewById(R.id.quantity_3);
        quantity4 = findViewById(R.id.quantity_4);
        quantity5 = findViewById(R.id.quantity_5);

        quantity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity1.setBackground(getResources().getDrawable(R.drawable.quantity_button_pressed));
                quantity2.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity3.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity4.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity5.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity = 1;
            }
        });

        quantity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity1.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity2.setBackground(getResources().getDrawable(R.drawable.quantity_button_pressed));
                quantity3.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity4.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity5.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity = 2;
            }
        });

        quantity3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity1.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity2.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity3.setBackground(getResources().getDrawable(R.drawable.quantity_button_pressed));
                quantity4.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity5.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity = 3;
            }
        });

        quantity4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity1.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity2.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity3.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity4.setBackground(getResources().getDrawable(R.drawable.quantity_button_pressed));
                quantity5.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity = 4;
            }
        });

        quantity5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity1.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity2.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity3.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity4.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity5.setBackground(getResources().getDrawable(R.drawable.quantity_button_pressed));
                quantity = 5;
            }
        });

        otherQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                quantity1.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity2.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity3.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity4.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                quantity5.setBackground(getResources().getDrawable(R.drawable.quantity_button));
                String qt = s.toString().toLowerCase();
                if (qt.isEmpty()) {
                    quantity = 0;
                } else {
                    quantity = Integer.valueOf(qt);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

}