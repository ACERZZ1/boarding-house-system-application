package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.activities.DetailActivity;
import com.example.boardinghousefinder.activities.FullListActivity;
import com.example.boardinghousefinder.models.BoardingHouse;

import java.util.List;

public class HorizontalBoardingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<BoardingHouse> list;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_MORE = 1;

    public HorizontalBoardingAdapter(Context context, List<BoardingHouse> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position == list.size() ? TYPE_MORE : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size() + 1; // +1 for "More" button
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MORE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_more, parent, false);
            return new MoreViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_boarding_house, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MoreViewHolder) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullListActivity.class);
                context.startActivity(intent);
            });
            return;
        }

        BoardingHouse item = list.get(position);
        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        viewHolder.name.setText(item.getTitle());           // was getName()
        viewHolder.price.setText(item.getPrice());
        viewHolder.description.setText(item.getDescription());

        // Load image from URL instead of drawable resource
        Glide.with(context)
                .load(item.getCoverImageUrl())               // was getImage()
                .placeholder(R.drawable.test2)
                .error(R.drawable.test2)
                .centerCrop()
                .into(viewHolder.image);

        viewHolder.itemView.setOnClickListener(v -> {
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
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, description;

        public ItemViewHolder(View itemView) {
            super(itemView);
            image       = itemView.findViewById(R.id.imageHouse);
            name        = itemView.findViewById(R.id.textName);
            price       = itemView.findViewById(R.id.textPrice);
            description = itemView.findViewById(R.id.textDescription);
        }
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {
        public MoreViewHolder(View itemView) {
            super(itemView);
        }
    }
}
