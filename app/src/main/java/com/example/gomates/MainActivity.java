package com.example.gomates;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomates.database.MySQLDatabaseHelper;
import com.example.gomates.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private Button postRideButton;
    private Button viewRidesButton;
    private Button logoutButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                MySQLDatabaseHelper.initializeDatabase();
                return null;
            }
        }.execute();

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        postRideButton = findViewById(R.id.post_ride_button);
        viewRidesButton = findViewById(R.id.view_rides_button);
        logoutButton = findViewById(R.id.logout_button);

        postRideButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, RidePostActivity.class)));

        viewRidesButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, RideListActivity.class)));

        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}