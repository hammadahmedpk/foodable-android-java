package com.foodable_android_java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

        // set the profile image
        Glide.with(getApplicationContext()).load(Url).circleCrop().into(ProfileImage);




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
                HandleSubmit();
            }
        });
    }
    private void HandleSubmit() {
        if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || bio.getText().toString().isEmpty()) {
            Toast.makeText(EditProfile.this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dpp == null) {
            Toast toast = Toast.makeText(EditProfile.this, "Please Select the profile picture", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        String str= "";
        // upload the image to firebase storage
        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        StorageReference ref= storageReference.child("profileImg/"+User.getUid());
        ref.putFile(dpp)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // save the user to firebase firestore
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                Map<String, Object> user1 = new HashMap<>();
                                user1.put("firstName", firstName.getText().toString());
                                user1.put("lastName", lastName.getText().toString());
                                user1.put("bio", bio.getText().toString());
                                user1.put("profile", uri.toString());
                                SharedPreferences putUser = getSharedPreferences("userProfile",MODE_PRIVATE);
                                SharedPreferences.Editor editor = putUser.edit();
                                editor.putString("firstName", firstName.getText().toString());
                                editor.putString("lastName", lastName.getText().toString());
                                editor.putString("bio", bio.getText().toString());
                                editor.putString("profile", uri.toString());
                                editor.apply();

                                // save userInfo
                                DatabaseReference myRef = database.getReference("users");
                                myRef.child(User.getUid()).setValue(user1);
                                Toast.makeText(EditProfile.this, "Update successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
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