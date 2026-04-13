package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.models.BoardingHouse;

import java.util.List;

public class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int propertyId, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(BoardingHouse house);
    }

    private final Context context;
    private final List<BoardingHouse> houses;
    private final OnDeleteClickListener deleteListener;
    private OnItemClickListener itemClickListener;

    public MyListingAdapter(Context context, List<BoardingHouse> houses,
                            OnDeleteClickListener deleteListener) {
        this.context = context;
        this.houses = houses;
        this.deleteListener = deleteListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_my_listing, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardingHouse house = houses.get(position);

        holder.tvTitle.setText(house.getTitle());
        holder.tvType.setText(house.getPropertyType());
        holder.tvLocation.setText(house.getCity() + ", " + house.getProvince());
        holder.tvPrice.setText(house.getPrice() + "/month");

        Glide.with(context)
                .load(house.getCoverImageUrl())
                .placeholder(android.R.color.darker_gray)
                .centerCrop()
                .into(holder.ivCover);

        // Whole card click → open OwnerDetailActivity
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(house);
            }
        });

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(house.getId(), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvType, tvLocation, tvPrice;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover    = itemView.findViewById(R.id.ivMyListingCover);
            tvTitle    = itemView.findViewById(R.id.tvMyListingTitle);
            tvType     = itemView.findViewById(R.id.tvMyListingType);
            tvLocation = itemView.findViewById(R.id.tvMyListingLocation);
            tvPrice    = itemView.findViewById(R.id.tvMyListingPrice);
            btnDelete  = itemView.findViewById(R.id.btnDeleteListing);
        }
    }
}
