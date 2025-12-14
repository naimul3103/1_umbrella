package com.nasimabc.a1umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etAddress, etPassword;
    private Switch switchProvider;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    
    // New Fields
    private LinearLayout layoutProviderFields;
    private Spinner spinnerServiceCategory;
    private EditText etHourlyRate, etAbout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        switchProvider = findViewById(R.id.switch_provider);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
        
        layoutProviderFields = findViewById(R.id.layout_provider_fields);
        spinnerServiceCategory = findViewById(R.id.spinner_service_category);
        etHourlyRate = findViewById(R.id.et_hourly_rate);
        etAbout = findViewById(R.id.et_about);

        // Setup Spinner
        String[] categories = {"Electrician", "Plumber", "Painter", "AC Repair", "Appliances", "Gas Service", "Door Lock", "Furniture", "Kitchen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerServiceCategory.setAdapter(adapter);

        // Handle Switch Toggle
        switchProvider.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutProviderFields.setVisibility(View.VISIBLE);
            } else {
                layoutProviderFields.setVisibility(View.GONE);
            }
        });

        // Handle Register Button Click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                boolean isProvider = switchProvider.isChecked();
                
                String category = "";
                String rate = "";
                String about = "";

                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (isProvider) {
                   if (etHourlyRate.getText().toString().isEmpty()) {
                       Toast.makeText(RegisterActivity.this, "Please enter your hourly rate", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   
                   // Capture provider fields safely
                   if (spinnerServiceCategory.getSelectedItem() != null) {
                       category = spinnerServiceCategory.getSelectedItem().toString();
                   }
                   rate = etHourlyRate.getText().toString();
                   about = etAbout.getText().toString();
                }

                // Show Progress and Disable Button
                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);

                registerUser(email, password, fullName, phone, address, isProvider, category, rate, about);
            }
        });

        // Handle Login Link Click
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser(String email, String password, String fullName, String phone, String address, boolean isProvider,
                              String category, String rate, String about) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        
                        if (user != null) {
                            // 1. Update Profile (Fire and Forget) - Do not wait for this to complete
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();
                            user.updateProfile(profileUpdates);
                            
                            // 2. Save detailed info to Firestore immediately
                            saveUserToFirestore(user.getUid(), fullName, email, phone, address, isProvider, category, rate, about);
                            
                        } else {
                            handleFailure("Registration failed: User is null");
                        }

                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        handleFailure("Authentication failed: " + errorMessage);
                    }
                });
    }
    
    private void saveUserToFirestore(String uid, String name, String email, String phone, String address, boolean isProvider,
                                     String category, String rate, String about) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", name);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("address", address);
        userMap.put("isProvider", isProvider);
        
        if (isProvider) {
            userMap.put("serviceCategory", category);
            userMap.put("hourlyRate", rate);
            userMap.put("about", about);
        }
        
        db.collection("users").document(uid).set(userMap)
            .addOnSuccessListener(aVoid -> {
                 progressBar.setVisibility(View.GONE);

                 String role = isProvider ? "Service Provider" : "Customer";
                 Toast.makeText(RegisterActivity.this, "Registration successful as " + role, Toast.LENGTH_SHORT).show();
                 
                 Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
                 finish();
            })
            .addOnFailureListener(e -> {
                handleFailure("User Created but Failed to Save Profile: " + e.getMessage());
            });
    }

    private void handleFailure(String message) {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
