package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.models.Section;

import java.util.List;

import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    Context context;
    List<Section> sectionList;

    public SectionAdapter(Context context, List<Section> sectionList) {
        this.context = context;
        this.sectionList = sectionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Section section = sectionList.get(position);
        holder.title.setText(section.getTitle());

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setClipToPadding(false);
        holder.recyclerView.setPadding(12, 0, 12, 0);

        HorizontalBoardingAdapter adapter =
                new HorizontalBoardingAdapter(context, section.getBoardingList());

        holder.recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        if (holder.recyclerView.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(holder.recyclerView);
        }

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            recyclerView = itemView.findViewById(R.id.recyclerHorizontal);
        }
    }
}
