package com.nasimabc.a1umbrella;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private final List<Provider> providerList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Provider provider);
    }

    public ProviderAdapter(List<Provider> providerList, OnItemClickListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider_card, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Provider provider = providerList.get(position);
        holder.bind(provider, listener);
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvRating;
        TextView tvDistance;
        TextView tvDescription;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_provider_name);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        public void bind(final Provider provider, final OnItemClickListener listener) {
            tvName.setText(provider.name);
            tvRating.setText(String.valueOf(provider.rating));
            tvDistance.setText(provider.distance);
            if (tvDescription != null) {
                tvDescription.setText(provider.description);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(provider));
        }
    }
}
