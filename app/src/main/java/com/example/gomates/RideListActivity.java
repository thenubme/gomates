package com.example.gomates;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomates.adapters.RideAdapter;
import com.example.gomates.models.Ride;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RideListActivity extends AppCompatActivity implements RideAdapter.OnRideClickListener {
    private RecyclerView recyclerView;
    private RideAdapter adapter;
    private List<Ride> rides;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);

        db = FirebaseFirestore.getInstance();
        rides = new ArrayList<>();

        recyclerView = findViewById(R.id.rides_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RideAdapter(rides, this);
        recyclerView.setAdapter(adapter);

        loadRides();
    }

    private void loadRides() {
        db.collection("rides")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rides.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ride ride = document.toObject(Ride.class);
                            ride.setId(document.getId());
                            rides.add(ride);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onRideClick(Ride ride) {
        Intent intent = new Intent(this, StaticMapActivity.class);
        intent.putExtra("origin", ride.getOrigin());
        intent.putExtra("destination", ride.getDestination());
        startActivity(intent);
    }
}
