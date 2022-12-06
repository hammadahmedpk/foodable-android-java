package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Signin extends AppCompatActivity {

    Button signin, show_password;
    FirebaseAuth auth;
    EditText email, Password;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove top bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        setContentView(R.layout.activity_signin);
        signin  = findViewById(R.id.signin);
        email   = findViewById(R.id.email);
        Password= findViewById(R.id.password);

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
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(Signin.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), History.class);
                                startActivity(intent);
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