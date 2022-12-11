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
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    Button createProfile;
    ImageView ProfileImage;
    EditText firstName, lastName , bio;
    Uri dpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        createProfile = findViewById(R.id.createProfile);

        firstName= findViewById(R.id.firstName);
        lastName= findViewById(R.id.lastName);
        bio= findViewById(R.id.bio);
        ProfileImage= findViewById(R.id.dp);

        SharedPreferences putUser = getSharedPreferences("userProfile",MODE_PRIVATE);
        firstName.setText(putUser.getString("firstName",""));
        lastName.setText(putUser.getString("lastName",""));
        bio.setText(putUser.getString("bio",""));
        String Url = putUser.getString("profile","");
        String edit_history= putUser.getString("edit","");
        // set the profile image
        Glide.with(getApplicationContext()).load(Url).circleCrop().signature(new ObjectKey(edit_history)).into(ProfileImage);
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
    private void HandleSubmit() {
        if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || bio.getText().toString().isEmpty()) {
            Toast.makeText(EditProfile.this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        String str= "";
        // upload the image to firebase storage
        FirebaseStorage storage= FirebaseStorage.getInstance();
        // save the user to firebase firestore
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("firstName", firstName.getText().toString());
        user1.put("lastName", lastName.getText().toString());
        user1.put("bio", bio.getText().toString());
        user1.put("profile", "https://foodgive.000webhostapp.com/Images/"+User.getUid()+".jpg");
        SharedPreferences putUser = getSharedPreferences("userProfile",MODE_PRIVATE);
        SharedPreferences.Editor editor = putUser.edit();
        editor.putString("firstName", firstName.getText().toString());
        editor.putString("lastName", lastName.getText().toString());
        editor.putString("bio", bio.getText().toString());
        editor.putString("profile", "https://foodgive.000webhostapp.com/Images/"+User.getUid()+".jpg");
        editor.putString("edit", String.valueOf(System.currentTimeMillis()));
        editor.apply();

        // save userInfo
        DatabaseReference myRef = database.getReference("users");
        myRef.child(User.getUid()).setValue(user1);
        Toast.makeText(EditProfile.this, "Update successful", Toast.LENGTH_SHORT).show();
    }


    public void uploadUserImage(){
        if (dpp == null) {
            Toast toast = Toast.makeText(EditProfile.this, "Please Select the profile picture", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(dpp);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);
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
                                    Toast.makeText(EditProfile.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // write the response to log
                                Log.d("Id", "Some thing went wrong");
                                e.printStackTrace();
                                Toast.makeText(EditProfile.this,"Incorrect JSON Image", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // print the error
                            Log.d("Id", error.toString());
                            Toast.makeText(EditProfile.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
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
            RequestQueue queue = Volley.newRequestQueue(EditProfile.this);
            queue.add(request);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dpp = data.getData();
            Glide.with(getApplicationContext()).load(dpp).circleCrop().into(ProfileImage);
        }
    }


}