package com.nasimabc.a1umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tv_welcome);
        etSearch = findViewById(R.id.et_search);

        // Set Welcome Message
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            tvWelcome.setText("Welcome, " + user.getDisplayName());
        } else {
            tvWelcome.setText("Welcome, User");
        }
        
        // Setup Search Functionality
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                
                String query = v.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ProviderListActivity.class);
                    intent.putExtra("SEARCH_QUERY", query);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });


        // Setup Services Grid Clicks
        setupServiceCard(R.id.card_electrician, "Electrician");
        setupServiceCard(R.id.card_plumber, "Plumber");
        setupServiceCard(R.id.card_painter, "Painter");
        setupServiceCard(R.id.card_ac, "AC Repair");
        setupServiceCard(R.id.card_appliances, "Appliances");
        setupServiceCard(R.id.card_gas, "Gas Service");
        setupServiceCard(R.id.card_doorlock, "Door Lock");
        setupServiceCard(R.id.card_furniture, "Furniture");
        setupServiceCard(R.id.card_kitchen, "Kitchen");

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_bookings) {
                    startActivity(new Intent(MainActivity.this, MyBookingsActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void setupServiceCard(int cardId, String category) {
        CardView card = findViewById(cardId);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProviderListActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        });
    }
}
