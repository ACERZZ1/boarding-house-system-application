package com.example.boardinghousefinder.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ArrayList<String> images = getIntent().getStringArrayListExtra("images");
        int startIndex = getIntent().getIntExtra("startIndex", 0);

        if (images == null || images.isEmpty()) {
            finish();
            return;
        }

        ViewPager2 viewPager = findViewById(R.id.viewPagerImages);
        TextView tvCounter  = findViewById(R.id.tvImageCounter);
        ImageView btnClose  = findViewById(R.id.btnCloseViewer);

        // Update counter on page change
        tvCounter.setText((startIndex + 1) + " / " + images.size());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tvCounter.setText((position + 1) + " / " + images.size());
            }
        });

        viewPager.setAdapter(new ImagePagerAdapter(images));
        viewPager.setCurrentItem(startIndex, false);

        btnClose.setOnClickListener(v -> finish());
    }

    // Simple inline adapter for the ViewPager2
    static class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

        ArrayList<String> urls;

        ImagePagerAdapter(ArrayList<String> urls) {
            this.urls = urls;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setBackgroundColor(0xFF000000);
            return new ImageViewHolder(iv);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            Glide.with(holder.imageView.getContext())
                    .load(urls.get(position))
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() { return urls.size(); }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageViewHolder(ImageView iv) {
                super(iv);
                imageView = iv;
            }
        }
    }
}
