package com.example.gomates;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomates.models.Ride;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RidePostActivity extends AppCompatActivity {
    private EditText originInput;
    private EditText destinationInput;
    private EditText fareInput;
    private Button postButton;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_post);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        originInput = findViewById(R.id.origin_input);
        destinationInput = findViewById(R.id.destination_input);
        fareInput = findViewById(R.id.fare_input);
        postButton = findViewById(R.id.post_button);

        postButton.setOnClickListener(v -> postRide());
    }

    private void postRide() {
        String origin = originInput.getText().toString().trim();
        String destination = destinationInput.getText().toString().trim();
        String fareStr = fareInput.getText().toString().trim();

        if (origin.isEmpty() || destination.isEmpty() || fareStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double fare = Double.parseDouble(fareStr);
        String driverId = mAuth.getCurrentUser().getUid();

        Ride ride = new Ride(origin, destination, fare, driverId);

        db.collection("rides")
                .add(ride)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ride posted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post ride", Toast.LENGTH_SHORT).show();
                });
    }
}
