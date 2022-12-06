package com.foodable_android_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class History extends AppCompatActivity {

    RecyclerView rv;
    HistoryAdapter adapter;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        });

        // A function that adds 3-4 items to database for testing
        addHistory();

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<HistoryModel> options = new FirebaseRecyclerOptions.Builder<HistoryModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("history").child(FirebaseAuth.getInstance().getUid()).orderByChild("name"), HistoryModel.class)
                .build();

        adapter = new HistoryAdapter(options);
        rv.setAdapter(adapter);

    }

    private void addHistory() {
        HistoryModel m1 = new HistoryModel("Ali Khan", "Donor", "An apple pie");
        HistoryModel m2 = new HistoryModel("Hammad Ahmed", "Donor", "3 donuts and yummy cup cakes");
        HistoryModel m3 = new HistoryModel("Babar Azam", "Receiver", "Anything but one down!");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("history").child(FirebaseAuth.getInstance().getUid());
        dbRef.child("1").setValue(m1);
        dbRef.child("2").setValue(m2);
        dbRef.child("3").setValue(m3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}