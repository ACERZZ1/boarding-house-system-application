package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.activities.DetailActivity;
import com.example.boardinghousefinder.models.BoardingHouse;

import java.util.List;

public class BoardingHouseAdapter extends RecyclerView.Adapter<BoardingHouseAdapter.ViewHolder> {

    Context context;
    List<BoardingHouse> list;

    public BoardingHouseAdapter(Context context, List<BoardingHouse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_boarding_house, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardingHouse house = list.get(position);

        holder.tvTitle.setText(house.getTitle());
        holder.tvPrice.setText(house.getPrice() + "/month");
        holder.tvLocation.setText(house.getCity() + ", " + house.getProvince());
        holder.tvType.setText(house.getPropertyType());

        Glide.with(context)
                .load(house.getCoverImageUrl())
                .placeholder(R.drawable.test2)
                .error(R.drawable.test2)
                .centerCrop()
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id",           house.getId());
            intent.putExtra("title",         house.getTitle());
            intent.putExtra("propertyType",  house.getPropertyType());
            intent.putExtra("price",         house.getPrice());
            intent.putExtra("deposit",       house.getDeposit());
            intent.putExtra("description",   house.getDescription());
            intent.putExtra("rules",         house.getRules());
            intent.putExtra("services",      house.getServices());
            intent.putExtra("maxGuest",      house.getMaxGuest());
            intent.putExtra("bedrooms",      house.getBedrooms());
            intent.putExtra("beds",          house.getBeds());
            intent.putExtra("bathrooms",     house.getBathrooms());
            intent.putExtra("streetAddress", house.getStreetAddress());
            intent.putExtra("city",          house.getCity());
            intent.putExtra("province",      house.getProvince());
            intent.putExtra("zipCode",       house.getZipCode());
            intent.putExtra("country",       house.getCountry());
            intent.putExtra("firstName",     house.getFirstName());
            intent.putExtra("lastName",      house.getLastName());
            intent.putExtra("email",         house.getEmail());
            intent.putExtra("phone",         house.getPhone());
            intent.putExtra("coverImageUrl", house.getCoverImageUrl());
            intent.putExtra("latitude", house.getLatitude());
            intent.putExtra("longitude", house.getLongitude());
            intent.putExtra("is_reserved", house.getIsReserved()); // Pass reservation status

            intent.putStringArrayListExtra("galleryImageUrls", house.getGalleryImageUrls());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvPrice, tvLocation, tvType;

        ViewHolder(View itemView) {
            super(itemView);
            ivCover    = itemView.findViewById(R.id.ivCover);
            tvTitle    = itemView.findViewById(R.id.tvTitle);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvType     = itemView.findViewById(R.id.tvType);
        }
    }
}
