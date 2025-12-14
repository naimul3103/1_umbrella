package com.nasimabc.a1umbrella;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvProviderName.setText(booking.getProviderName());
        holder.tvServiceType.setText(booking.getServiceType());
        holder.tvStatus.setText(booking.getStatus());
        holder.tvDate.setText("Date: " + booking.getDate());
        holder.tvTime.setText("Time: " + booking.getTime());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvProviderName, tvServiceType, tvStatus, tvDate, tvTime;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProviderName = itemView.findViewById(R.id.tv_booking_provider);
            tvServiceType = itemView.findViewById(R.id.tv_booking_service);
            tvStatus = itemView.findViewById(R.id.tv_booking_status);
            tvDate = itemView.findViewById(R.id.tv_booking_date);
            tvTime = itemView.findViewById(R.id.tv_booking_time);
        }
    }
}
