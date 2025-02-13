
package com.example.gomates;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.example.gomates.database.MySQLHelper;

public class MainActivity extends AppCompatActivity {
    private Button postRideButton;
    private Button viewRidesButton;
    private Button logoutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                MySQLHelper.initializeDatabase();
                return null;
            }
        }.execute();

        mAuth = FirebaseAuth.getInstance();

        postRideButton = findViewById(R.id.post_ride_button);
        viewRidesButton = findViewById(R.id.view_rides_button);
        logoutButton = findViewById(R.id.logout_button);

        postRideButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, RidePostActivity.class)));

        viewRidesButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, RideListActivity.class)));

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
