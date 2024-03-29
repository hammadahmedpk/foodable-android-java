package com.foodable_android_java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    ImageView male, female, other;
    String gender;
    Button createProfile;

    ImageView ProfileImage;
    EditText firstName, lastName , bio;
    Uri dpp;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        setContentView(R.layout.activity_create_profile);
        createProfile = findViewById(R.id.createProfile);
        male= findViewById(R.id.male);
        female= findViewById(R.id.female);
        other= findViewById(R.id.other);
        // set the gender on Click listener
        setGender();

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        firstName= findViewById(R.id.firstName);
        lastName= findViewById(R.id.lastName);
        bio= findViewById(R.id.bio);
        ProfileImage= findViewById(R.id.dp);
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Choose your DP"), 100);
            }
        });

        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserImage();
            }
        });
    }


    public void uploadUserImage(){
        try {
            InputStream inputStream = getContentResolver().openInputStream(dpp);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);
            System.out.println(encodedImg);
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    "https://foodgive.000webhostapp.com/profileUpload.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj=new JSONObject(response);
                                if(obj.getInt("code")==1)
                                {
                                    HandleSubmit();
                                }
                                else{
                                    Toast.makeText(CreateProfile.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // write the response to log
                                Log.d("Id", "Some thing went wrong");
                                e.printStackTrace();
                                Toast.makeText(CreateProfile.this,"Incorrect JSON Image", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // print the error
                            Log.d("Id", error.toString());
                            Toast.makeText(CreateProfile.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params=new HashMap<>();
                    String userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                    params.put("id", FirebaseAuth.getInstance().getUid().toString());
                    params.put("profileUrl", encodedImg);
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue queue = Volley.newRequestQueue(CreateProfile.this);
            queue.add(request);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void HandleSubmit() {
        if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || bio.getText().toString().isEmpty()) {
            Toast.makeText(CreateProfile.this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dpp == null) {
            Toast toast = Toast.makeText(CreateProfile.this, "Please Select the profile picture", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        // upload the image to firebase storage
        FirebaseStorage storage= FirebaseStorage.getInstance();
        // save the user to firebase firestore
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("firstName", firstName.getText().toString());
        user1.put("lastName", lastName.getText().toString());
        user1.put("bio", bio.getText().toString());
        user1.put("profile","https://foodgive.000webhostapp.com/Images/"+User.getUid()+".jpg");
        user1.put("gender", gender);
        user1.put("email", email);
        user1.put("password", password);
        user1.put("uid", User.getUid());
        Toast.makeText(CreateProfile.this, email, Toast.LENGTH_SHORT).show();
        SharedPreferences putUser = getSharedPreferences("userProfile",MODE_PRIVATE);
        SharedPreferences.Editor editor = putUser.edit();
        editor.putString("firstName", firstName.getText().toString());
        editor.putString("lastName", lastName.getText().toString());
        editor.putString("bio", bio.getText().toString());
        editor.putString("profile", "https://foodgive.000webhostapp.com/Images/"+User.getUid()+".jpg");
        editor.apply();

        OneSignal.setExternalUserId(User.getUid(), new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
            @Override
            public void onSuccess(JSONObject results) {
                try {
                    if (results.has("push") && results.getJSONObject("push").has("success")) {
                        boolean isPushSuccess = results.getJSONObject("push").getBoolean("success");
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for push status: " + isPushSuccess);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OneSignal.ExternalIdError error) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id done with error: " + error.toString());
            }
        });

        // save userInfo
        DatabaseReference myRef = database.getReference("users");
        myRef.child(User.getUid()).setValue(user1);
        Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CreateProfile.this, Home.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dpp = data.getData();
            ProfileImage.setImageURI(dpp);
        }
    }


    public void setGender(){
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set background color to orange
                male.setBackgroundColor(getResources().getColor(R.color.orange));
                female.setBackgroundColor(getResources().getColor(R.color.white));
                other.setBackgroundColor(getResources().getColor(R.color.white));
                gender= "male";
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set background color to orange
                male.setBackgroundColor(getResources().getColor(R.color.white));
                female.setBackgroundColor(getResources().getColor(R.color.orange));
                other.setBackgroundColor(getResources().getColor(R.color.white));
                gender= "female";
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set background color to orange
                male.setBackgroundColor(getResources().getColor(R.color.white));
                female.setBackgroundColor(getResources().getColor(R.color.white));
                other.setBackgroundColor(getResources().getColor(R.color.orange));
                gender= "other";
            }
        });
    }
}