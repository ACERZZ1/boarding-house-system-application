package com.example.boardinghousefinder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.adapters.FlexibleAdapter;
import com.example.boardinghousefinder.models.BoardingHouse;
import com.example.boardinghousefinder.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    RecyclerView recyclerView;
    List<BoardingHouse> list;

    public CategoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerCategory);
        list = new ArrayList<>();

        // Updated sample data to match the new 26-parameter BoardingHouse constructor
        // Added '0' at the end for the isReserved status
        list.add(new BoardingHouse(-1, "Affordable Room", "Boarding House", "₱1500", "0", "Budget friendly", "", "", "0", "0", "0", "0", "", "", "", "", "", "", "", "", "", 0.0, 0.0, "", new ArrayList<>(), 0));
        list.add(new BoardingHouse(-1, "Near School Stay", "Boarding House", "₱2500", "0", "5 mins walk", "", "", "0", "0", "0", "0", "", "", "", "", "", "", "", "", "", 0.0, 0.0, "", new ArrayList<>(), 0));
        list.add(new BoardingHouse(-1, "WiFi Ready", "Boarding House", "₱3000", "0", "Fast internet", "", "", "0", "0", "0", "0", "", "", "", "", "", "", "", "", "", 0.0, 0.0, "", new ArrayList<>(), 0));
        list.add(new BoardingHouse(-1, "Popular Place", "Boarding House", "₱2800", "0", "Highly rated", "", "", "0", "0", "0", "0", "", "", "", "", "", "", "", "", "", 0.0, 0.0, "", new ArrayList<>(), 0));
        list.add(new BoardingHouse(-1, "Cheap Bedspace", "Bedspace", "₱1200", "0", "Very affordable", "", "", "0", "0", "0", "0", "", "", "", "", "", "", "", "", "", 0.0, 0.0, "", new ArrayList<>(), 0));

        FlexibleAdapter adapter = new FlexibleAdapter(getContext(), list);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int spacing = 12;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
