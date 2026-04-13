package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.activities.DetailActivity;
import com.example.boardinghousefinder.models.BoardingHouse;

import java.util.List;

public class FlexibleAdapter extends RecyclerView.Adapter<FlexibleAdapter.ViewHolder> {

    Context context;
    List<BoardingHouse> list;

    public FlexibleAdapter(Context context, List<BoardingHouse> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, description;

        public ViewHolder(View itemView) {
            super(itemView);
            image       = itemView.findViewById(R.id.imageHouse);
            name        = itemView.findViewById(R.id.textName);
            price       = itemView.findViewById(R.id.textPrice);
            description = itemView.findViewById(R.id.textDescription);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_boarding_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BoardingHouse item = list.get(position);

        holder.name.setText(item.getTitle());
        holder.price.setText(item.getPrice());
        holder.description.setText(item.getDescription());

        // Load image from URL instead of drawable resource
        Glide.with(context)
                .load(item.getCoverImageUrl())
                .placeholder(R.drawable.test2)
                .error(R.drawable.test2)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("title",         item.getTitle());
            intent.putExtra("propertyType",  item.getPropertyType());
            intent.putExtra("price",         item.getPrice());
            intent.putExtra("deposit",       item.getDeposit());
            intent.putExtra("description",   item.getDescription());
            intent.putExtra("rules",         item.getRules());
            intent.putExtra("services",      item.getServices());
            intent.putExtra("maxGuest",      item.getMaxGuest());
            intent.putExtra("bedrooms",      item.getBedrooms());
            intent.putExtra("beds",          item.getBeds());
            intent.putExtra("bathrooms",     item.getBathrooms());
            intent.putExtra("streetAddress", item.getStreetAddress());
            intent.putExtra("city",          item.getCity());
            intent.putExtra("province",      item.getProvince());
            intent.putExtra("zipCode",       item.getZipCode());
            intent.putExtra("country",       item.getCountry());
            intent.putExtra("firstName",     item.getFirstName());
            intent.putExtra("lastName",      item.getLastName());
            intent.putExtra("email",         item.getEmail());
            intent.putExtra("phone",         item.getPhone());
            intent.putExtra("coverImageUrl", item.getCoverImageUrl());
            context.startActivity(intent);
        });

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_anim);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<BoardingHouse> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
