package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signin extends AppCompatActivity {

    Button signin, show_password;
    FirebaseAuth auth;
    EditText email, Password;
    TextView signUp;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove top bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        setContentView(R.layout.activity_signin);
        signin  = findViewById(R.id.signin);
        email   = findViewById(R.id.email);
        Password= findViewById(R.id.password);

        forgotPassword = findViewById(R.id.forgotPassword);
        signUp= findViewById(R.id.signUp);

        auth= FirebaseAuth.getInstance();
        show_password= findViewById(R.id.show_password);

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Password.getInputType() == 129) {
                    Password.setInputType(1);
                } else {
                    Password.setInputType(129);
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signin.this, ForgotPassword.class);
                startActivity(intent);
            }
        });



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Signin.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(email.getText().toString() , Password.getText().toString())
                        .addOnCompleteListener(Signin.this, task -> {
                            if (task.isSuccessful()) {
                                // Get the user Information
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String uid = auth.getCurrentUser().getUid();
                                OneSignal.setExternalUserId(uid, new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
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
                                DatabaseReference myRef = database.getReference("users").child(uid);

                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        // print the user information
                                        Log.d("user", user.toString());
                                        if(user!=null){
                                            SharedPreferences putUser = getSharedPreferences("userProfile",MODE_PRIVATE);
                                            SharedPreferences.Editor editor = putUser.edit();
                                            editor.putString("firstName", user.getFirstName());
                                            editor.putString("lastName",user.getLastName());
                                            editor.putString("bio", user.getBio());
                                            editor.putString("profile",user.getProfile());
                                            editor.apply();
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(Signin.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), Home.class);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        System.out.println("The read failed: " + databaseError.getCode());
                                    }
                                });
                            } else {
                                System.out.println("Sign in failed");
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Signin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}