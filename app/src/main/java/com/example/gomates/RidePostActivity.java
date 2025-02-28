package com.example.gomates;

import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gomates.models.Ride;
import com.example.gomates.database.MySQLDatabaseHelper;
import com.example.gomates.utils.SessionManager;

public class RidePostActivity extends AppCompatActivity {
    private EditText originInput;
    private EditText destinationInput;
    private EditText fareInput;
    private EditText seatsInput;
    private EditText departureTimeInput;
    private Button postButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_post);

        sessionManager = new SessionManager(this);

        // Initialize views
        originInput = findViewById(R.id.origin_input);
        destinationInput = findViewById(R.id.destination_input);
        fareInput = findViewById(R.id.fare_input);
        seatsInput = findViewById(R.id.seats_input);
        departureTimeInput = findViewById(R.id.departure_time_input);
        postButton = findViewById(R.id.post_button);

        postButton.setOnClickListener(v -> postRide());
    }

    private void postRide() {
        String origin = originInput.getText().toString().trim();
        String destination = destinationInput.getText().toString().trim();
        String fareStr = fareInput.getText().toString().trim();
        String seatsStr = seatsInput.getText().toString().trim();
        String departureTime = departureTimeInput.getText().toString().trim();

        // Validate inputs
        if (origin.isEmpty() || destination.isEmpty() || fareStr.isEmpty() || 
            seatsStr.isEmpty() || departureTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double fare = Double.parseDouble(fareStr);
            int seats = Integer.parseInt(seatsStr);
            int userId = Integer.parseInt(sessionManager.getUserId());

            Ride ride = new Ride();
            ride.setOrigin(origin);
            ride.setDestination(destination);
            ride.setFare(fare);
            ride.setAvailableSeats(seats);
            ride.setDepartureTime(departureTime);
            ride.setUserId(userId);

            new PostRideTask().execute(ride);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for fare and seats", 
                         Toast.LENGTH_SHORT).show();
        }
    }

    private class PostRideTask extends AsyncTask<Ride, Void, Long> {
        @Override
        protected Long doInBackground(Ride... rides) {
            try {
                return MySQLDatabaseHelper.insertRide(rides[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            if (result != -1) {
                Toast.makeText(RidePostActivity.this, "Ride posted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RidePostActivity.this, "Failed to post ride", Toast.LENGTH_SHORT).show();
            }
        }
    }
}