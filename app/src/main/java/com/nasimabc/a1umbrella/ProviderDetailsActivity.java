package com.nasimabc.a1umbrella;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

public class ProviderDetailsActivity extends AppCompatActivity {

    private TextView tvName, tvCategory, tvRating, tvDistance, tvDescription;
    private Button btnCall, btnBook;
    private Provider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_details);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize Views
        tvName = findViewById(R.id.tv_provider_name);
        tvCategory = findViewById(R.id.tv_provider_category);
        tvRating = findViewById(R.id.tv_provider_rating);
        tvDistance = findViewById(R.id.tv_provider_distance);
        tvDescription = findViewById(R.id.tv_provider_description);
        btnCall = findViewById(R.id.btn_call_now);
        btnBook = findViewById(R.id.btn_book_now);

        // Get Data
        String providerJson = getIntent().getStringExtra("provider_data");
        if (providerJson != null) {
            provider = new Gson().fromJson(providerJson, Provider.class);
            populateData();
        }

        // Call Button
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1234567890")); // Replace with actual number if available
            startActivity(intent);
        });

        // Book Button - Navigate to BookingActivity
        btnBook.setOnClickListener(v -> {
            if (provider != null) {
                Intent intent = new Intent(ProviderDetailsActivity.this, BookingActivity.class);
                intent.putExtra("PROVIDER_NAME", provider.name);
                intent.putExtra("PROVIDER_CATEGORY", provider.category);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error loading provider info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateData() {
        tvName.setText(provider.name);
        tvCategory.setText(provider.category);
        tvRating.setText(String.valueOf(provider.rating) + " (Reviews)");
        tvDistance.setText(provider.distance);
        tvDescription.setText(provider.description);
    }
}
