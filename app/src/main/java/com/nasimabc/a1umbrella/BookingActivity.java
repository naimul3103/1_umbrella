package com.nasimabc.a1umbrella;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private ChipGroup chipGroupTime;
    private EditText etAddress, etNotes;
    
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize Views
        TextView tvProviderName = findViewById(R.id.tv_booking_provider_name);
        TextView tvServiceType = findViewById(R.id.tv_booking_service_type);
        CalendarView calendarView = findViewById(R.id.calendar_view);
        chipGroupTime = findViewById(R.id.chip_group_time);
        etAddress = findViewById(R.id.et_booking_address);
        etNotes = findViewById(R.id.et_booking_notes);
        Button btnConfirm = findViewById(R.id.btn_confirm_booking);
        ImageButton btnBack = findViewById(R.id.btn_back_booking);

        // Get Data from Intent
        String providerName = getIntent().getStringExtra("PROVIDER_NAME");
        String providerCategory = getIntent().getStringExtra("PROVIDER_CATEGORY");

        if (providerName != null) {
            tvProviderName.setText(providerName);
        }
        if (providerCategory != null) {
            tvServiceType.setText(providerCategory);
        }
        
        // Default date (today)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = sdf.format(new Date(calendarView.getDate()));

        // Listen for Date Change
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month is 0-indexed
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Confirm Button Logic
        btnConfirm.setOnClickListener(v -> {
            // Get Selected Time
            int selectedChipId = chipGroupTime.getCheckedChipId();
            if (selectedChipId == -1) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }
            Chip selectedChip = findViewById(selectedChipId);
            String selectedTime = selectedChip.getText().toString();

            String address = etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                etAddress.setError("Address is required");
                return;
            }
            
            String notes = etNotes.getText().toString().trim();
            
            saveBooking(providerName, providerCategory, selectedDate, selectedTime, address, notes);
        });
    }

    private void saveBooking(String providerName, String category, String date, String time, String address, String notes) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null 
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() 
                : "guest_user"; // Handle logged out state properly in real app

        Booking booking = new Booking(
                userId,
                providerName,
                category,
                date,
                time,
                address,
                notes,
                "Pending"
        );

        FirebaseFirestore.getInstance().collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(BookingActivity.this, "Booking Confirmed!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(BookingActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
