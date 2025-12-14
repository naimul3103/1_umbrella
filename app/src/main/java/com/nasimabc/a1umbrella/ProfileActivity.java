package com.nasimabc.a1umbrella;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etAddress;
    private TextView tvRoleBadge;
    private ImageView imgProfile;
    private Button btnSave, btnPastServices, btnLogout, btnSupport;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    // Load immediately for preview
                    Glide.with(this).load(imageUri).circleCrop().into(imgProfile);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize Views
        etName = findViewById(R.id.et_profile_name);
        etPhone = findViewById(R.id.et_profile_phone);
        etAddress = findViewById(R.id.et_profile_address);
        tvRoleBadge = findViewById(R.id.tv_role_badge);
        imgProfile = findViewById(R.id.img_profile);
        btnSave = findViewById(R.id.btn_save_profile);
        btnPastServices = findViewById(R.id.btn_past_services);
        btnSupport = findViewById(R.id.btn_support); // New Support Button
        btnLogout = findViewById(R.id.btn_logout);

        // Load existing data
        loadUserData();

        // Listeners
        imgProfile.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveProfile());
        
        btnPastServices.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyBookingsActivity.class)));
        
        btnSupport.setOnClickListener(v -> {
            // Simple support action - Open Email
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@1umbrella.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - " + (mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "Guest"));
            try {
                startActivity(Intent.createChooser(emailIntent, "Contact Support"));
            } catch (Exception e) {
                Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            
            db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String phone = documentSnapshot.getString("phone");
                        String address = documentSnapshot.getString("address");
                        Boolean isProvider = documentSnapshot.getBoolean("isProvider");
                        String serviceCategory = documentSnapshot.getString("serviceCategory");
                        String photoUrl = documentSnapshot.getString("photoUrl");

                        if (fullName != null) etName.setText(fullName);
                        if (phone != null) etPhone.setText(phone);
                        if (address != null) etAddress.setText(address);
                        
                        // Load Profile Image using Glide
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(this)
                                 .load(photoUrl)
                                 .placeholder(android.R.drawable.ic_menu_my_calendar)
                                 .circleCrop()
                                 .into(imgProfile);
                        } else {
                            // Reset to default if no URL
                            imgProfile.setImageResource(android.R.drawable.ic_menu_my_calendar);
                            // If you want it circular even for placeholder:
                            Glide.with(this).load(android.R.drawable.ic_menu_my_calendar).circleCrop().into(imgProfile);
                        }

                        // Set Role Badge Logic
                        if (isProvider != null && isProvider) {
                            tvRoleBadge.setText("Service Provider" + (serviceCategory != null ? " (" + serviceCategory + ")" : ""));
                            tvRoleBadge.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                        } else {
                            tvRoleBadge.setText("Customer");
                            tvRoleBadge.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        }
                    }
                });

            if (etName.getText().toString().isEmpty() && user.getDisplayName() != null) {
                etName.setText(user.getDisplayName());
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        // Show simplified loading feedback
        btnSave.setText("Saving...");
        btnSave.setEnabled(false);

        if (imageUri != null) {
            uploadImageAndSave(name, phone, address);
        } else {
            updateFirestore(name, phone, address, null);
        }
    }

    private void uploadImageAndSave(String name, String phone, String address) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        
        StorageReference fileRef = storageRef.child(user.getUid() + ".jpg");
        
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    updateFirestore(name, phone, address, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Still save the text fields
                    updateFirestore(name, phone, address, null);
                });
    }

    private void updateFirestore(String name, String phone, String address, String photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                .update("fullName", name, "phone", phone, "address", address)
                .addOnSuccessListener(aVoid -> {
                    if (photoUrl != null) {
                        db.collection("users").document(user.getUid()).update("photoUrl", photoUrl);
                    }
                    Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    resetSaveButton();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resetSaveButton();
                });
        }
    }
    
    private void resetSaveButton() {
        btnSave.setText("Save Changes");
        btnSave.setEnabled(true);
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
