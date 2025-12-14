package com.nasimabc.a1umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ProviderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProviderAdapter adapter;
    private TextView tvTitle;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        tvTitle = findViewById(R.id.tv_category_title);
        recyclerView = findViewById(R.id.recycler_view_providers);
        progressBar = findViewById(R.id.progress_bar_providers);
        tvEmptyState = findViewById(R.id.tv_empty_state);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        db = FirebaseFirestore.getInstance();

        // Get data from intent
        String category = getIntent().getStringExtra("CATEGORY");
        String searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
        
        // Back Button Logic
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Update Title
        if (searchQuery != null && !searchQuery.isEmpty()) {
            tvTitle.setText("Results for \"" + searchQuery + "\"");
        } else if (category != null) {
            tvTitle.setText(category);
        } else {
            tvTitle.setText("Service Providers");
        }

        loadProviders(category, searchQuery);
    }

    private void loadProviders(String category, String searchQuery) {
        // Show Loading State
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        db.collection("users")
                .whereEqualTo("isProvider", true)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    List<Provider> realProviders = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("fullName");
                            String cat = document.getString("serviceCategory");
                            String desc = document.getString("about");
                            String address = document.getString("address");
                            
                            // Default values
                            double rating = 5.0; 
                            String distance = "Verified Pro"; 
                            String description = (desc != null && !desc.isEmpty()) ? desc : "No description provided.";
                            if (address != null && !address.isEmpty()) {
                                distance = address;
                            }

                            if (name != null && cat != null) {
                                boolean matches = false;
                                if (category != null) {
                                    if (cat.trim().equalsIgnoreCase(category.trim())) matches = true;
                                } else if (searchQuery != null) {
                                    String q = searchQuery.toLowerCase().trim();
                                    if (name.toLowerCase().contains(q) || cat.toLowerCase().contains(q)) matches = true;
                                } else {
                                    matches = true;
                                }

                                if (matches) {
                                    realProviders.add(new Provider(name, rating, distance, description, cat));
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error loading real providers: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    // Final List Logic
                    List<Provider> finalList = new ArrayList<>();
                    if (!realProviders.isEmpty()) {
                        finalList.addAll(realProviders);
                    } else {
                        finalList.addAll(generateMockProviders(category, searchQuery));
                    }
                    
                    if (finalList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new ProviderAdapter(finalList, this::onProviderClick);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    private void onProviderClick(Provider provider) {
        Intent intent = new Intent(this, ProviderDetailsActivity.class);
        String json = new Gson().toJson(provider);
        intent.putExtra("provider_data", json);
        startActivity(intent);
    }

    private List<Provider> generateMockProviders(String category, String searchQuery) {
        List<Provider> allProviders = new ArrayList<>();
        // Mock Data
        allProviders.add(new Provider("John Doe (Demo)", 4.8, "1.2 km", "Expert in residential Electrician work.", "Electrician"));
        allProviders.add(new Provider("Mike Smith (Demo)", 4.2, "3.0 km", "Industrial and home Electrician.", "Electrician"));
        allProviders.add(new Provider("Jane Smith (Demo)", 4.5, "2.5 km", "Professional Plumber services.", "Plumber"));
        allProviders.add(new Provider("Bob Johnson (Demo)", 3.9, "5.0 km", "Leakage specialist.", "Plumber"));
        allProviders.add(new Provider("Alice Brown (Demo)", 4.9, "1.0 km", "Wall and house Painter.", "Painter"));
        allProviders.add(new Provider("Cool Air Services", 4.6, "4.2 km", "AC repair and maintenance.", "AC Repair"));
        allProviders.add(new Provider("FixIt Appliances", 4.3, "2.1 km", "Fridge and TV repair.", "Appliances"));
        allProviders.add(new Provider("Gas Safe", 4.7, "3.5 km", "Gas stove and line repair.", "Gas Service"));
        allProviders.add(new Provider("Quick Plumb", 4.1, "6.0 km", "24/7 Plumbing service.", "Plumber"));
        allProviders.add(new Provider("Bright Lights", 4.5, "2.8 km", "Modern lighting solutions.", "Electrician"));
        allProviders.add(new Provider("Safe Locksmiths", 4.9, "1.5 km", "Emergency door opening and lock changes.", "Door Lock"));
        allProviders.add(new Provider("Secure Home", 4.7, "2.2 km", "Smart lock installation and repair.", "Door Lock"));
        allProviders.add(new Provider("Wood Works", 4.6, "3.0 km", "Sofa, table, and chair repairs.", "Furniture"));
        allProviders.add(new Provider("Antique Restorations", 4.8, "5.5 km", "Expert antique furniture restoration.", "Furniture"));
        allProviders.add(new Provider("Kitchen Masters", 4.7, "2.0 km", "Cabinet repair and kitchen remodeling.", "Kitchen"));
        allProviders.add(new Provider("Chef's Choice Repairs", 4.5, "4.0 km", "Countertop and sink fixes.", "Kitchen"));

        List<Provider> filtered = new ArrayList<>();
        for (Provider p : allProviders) {
            boolean matches = false;
            if (category != null) {
                if (p.category.equalsIgnoreCase(category)) matches = true;
            } else if (searchQuery != null) {
                String q = searchQuery.toLowerCase();
                if (p.name.toLowerCase().contains(q) || p.category.toLowerCase().contains(q)) matches = true;
            } else {
                matches = true;
            }
            if (matches) filtered.add(p);
        }
        return filtered;
    }
}
