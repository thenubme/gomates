package com.example.gomates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomates.R;
import com.example.gomates.models.Ride;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private List<Ride> rides;
    private OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
    }

    public RideAdapter(List<Ride> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.originText.setText(ride.getOrigin());
        holder.destinationText.setText(ride.getDestination());
        holder.fareText.setText(String.format("$%.2f", ride.getFare()));
        
        holder.itemView.setOnClickListener(v -> listener.onRideClick(ride));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView originText;
        TextView destinationText;
        TextView fareText;

        RideViewHolder(View itemView) {
            super(itemView);
            originText = itemView.findViewById(R.id.origin_text);
            destinationText = itemView.findViewById(R.id.destination_text);
            fareText = itemView.findViewById(R.id.fare_text);
        }
    }
}
