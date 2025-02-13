package com.example.gomates;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomates.adapters.RideAdapter;
import com.example.gomates.models.Ride;
import com.example.gomates.database.MySQLDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RideListActivity extends AppCompatActivity implements RideAdapter.OnRideClickListener {
    private RecyclerView recyclerView;
    private RideAdapter adapter;
    private List<Ride> rides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);

        rides = new ArrayList<>();
        recyclerView = findViewById(R.id.rides_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RideAdapter(rides, this);
        recyclerView.setAdapter(adapter);

        new LoadRidesTask().execute();
    }

    private class LoadRidesTask extends AsyncTask<Void, Void, List<Ride>> {
        @Override
        protected List<Ride> doInBackground(Void... voids) {
            return MySQLDatabaseHelper.getAllRides();
        }

        @Override
        protected void onPostExecute(List<Ride> loadedRides) {
            rides.clear();
            rides.addAll(loadedRides);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRideClick(Ride ride) {
        Intent intent = new Intent(this, StaticMapActivity.class);
        intent.putExtra("origin", ride.getOrigin());
        intent.putExtra("destination", ride.getDestination());
        startActivity(intent);
    }
}