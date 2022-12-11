package com.foodable_android_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<String> encodedImgs = new ArrayList<>();

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
                encodedImgs.clear();
                images.clear();
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
                }
                else
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
                    ArrayList<String> images1 = new ArrayList<>();
                    for (int i = 0; i < encodedImgs.size(); i++) {
                        String str = key + "_" + i;
                        DatabaseReference dbImgRef = dbRef.child("images").child(String.valueOf(i));
                        images1.add("https://foodgive.000webhostapp.com/Donation/" + str + ".jpg");
                    }
                    donation.setImages(images1);
                    dbRef.setValue(donation);
                    for (int i = 0; i < encodedImgs.size(); i++) {
                        String val= key+ "_" + i;
                        String encodedImage = encodedImgs.get(i);
                        RequestQueue queue;
                        StringRequest request=new StringRequest(
                                Request.Method.POST,
                                "https://foodgive.000webhostapp.com/donationImg.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            System.out.println("tagconvertstr::"+"["+response.toString()+"]");
                                            JSONObject obj=new JSONObject(response);
                                            if(obj.getInt("code")==1)
                                            {
                                                Toast.makeText(DonateActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                            else{
                                                Toast.makeText(DonateActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            System.out.println(e);
                                            e.printStackTrace();
                                            Toast.makeText(DonateActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(DonateActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                                    }
                                })
                        {
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params=new HashMap<>();
                                params.put("id",val );
                                params.put("DonImg", encodedImage);
                                return params;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue = Volley.newRequestQueue(DonateActivity.this);
                        queue.add(request);
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
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    encodedImgs.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
                    selectImage.setImageURI(imageUri);
                    images.add(imageUri);
                    isImageSelected = true;
                }
            }
            else {
                Uri imageUri = data.getData();
                images.add(imageUri);
                selectImage.setImageURI(imageUri);
                isImageSelected = true;
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                encodedImgs.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
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