package com.nasimabc.a1umbrella;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private TextView tvEmpty;
    private ImageButton btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        rvBookings = findViewById(R.id.rv_bookings);
        tvEmpty = findViewById(R.id.tv_empty_bookings);
        btnBack = findViewById(R.id.btn_back_bookings);

        btnBack.setOnClickListener(v -> finish());

        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        rvBookings.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadBookings();
    }

    private void loadBookings() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest_user";

        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        bookingList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Booking booking = document.toObject(Booking.class);
                            if (booking != null) {
                                booking.setBookingId(document.getId());
                                bookingList.add(booking);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        toggleEmptyState();
                    } else {
                        toggleEmptyState();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MyBookingsActivity.this, "Error loading bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleEmptyState() {
        if (bookingList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
        }
    }
}
