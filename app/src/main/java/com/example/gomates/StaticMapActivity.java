package com.example.gomates;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class StaticMapActivity extends AppCompatActivity {
    private ImageView mapImageView;
    private static final String MAP_API_KEY = "YOUR_API_KEY_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_map);

        mapImageView = findViewById(R.id.map_image_view);
        
        String origin = getIntent().getStringExtra("origin");
        loadStaticMap(origin);
    }

    private void loadStaticMap(String location) {
        String staticMapUrl = String.format(
            "https://maps.googleapis.com/maps/api/staticmap?center=%s" +
            "&zoom=13&size=600x300&markers=label:A|%s&key=%s",
            location, location, MAP_API_KEY
        );

        Glide.with(this)
                .load(staticMapUrl)
                .into(mapImageView);
    }
}
