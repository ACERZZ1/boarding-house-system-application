package com.example.boardinghousefinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.adapters.BoardingHouseAdapter;
import com.example.boardinghousefinder.models.BoardingHouse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Hardcoded IP as requested
    private static final String FETCH_URL = "http://192.168.254.104/casptone/fetch.php";

    RecyclerView recyclerView;
    List<BoardingHouse> houseList;
    BoardingHouseAdapter adapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHome);
        houseList = new ArrayList<>();

        adapter = new BoardingHouseAdapter(getContext(), houseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchBoardingHouses();

        return view;
    }

    private void fetchBoardingHouses() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                FETCH_URL,
                null,
                response -> {
                    if (!isAdded() || getContext() == null) return; // Prevent crash if fragment detached

                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            houseList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                ArrayList<String> galleryList = new ArrayList<>();
                                JSONArray galleryJson = obj.optJSONArray("gallery_image_urls");
                                if (galleryJson != null) {
                                    for (int j = 0; j < galleryJson.length(); j++) {
                                        galleryList.add(galleryJson.getString(j));
                                    }
                                }

                                BoardingHouse house = new BoardingHouse(
                                        obj.getInt("id"),
                                        obj.optString("title", ""),
                                        obj.optString("property_type", ""),
                                        "₱" + obj.optString("price", "0"),
                                        obj.optString("deposit", "0"),
                                        obj.optString("description", ""),
                                        obj.optString("rules", ""),
                                        obj.optString("services", ""),
                                        obj.optString("max_guest", "0"),
                                        obj.optString("bedrooms", "0"),
                                        obj.optString("beds", "0"),
                                        obj.optString("bathrooms", "0"),
                                        obj.optString("street_address", ""),
                                        obj.optString("city", ""),
                                        obj.optString("province", ""),
                                        obj.optString("zip_code", ""),
                                        obj.optString("country", ""),
                                        obj.optString("first_name", ""),
                                        obj.optString("last_name", ""),
                                        obj.optString("email", ""),
                                        obj.optString("phone", ""),
                                        obj.optDouble("latitude",0.0),
                                        obj.optDouble("longitude",0.0),
                                        obj.optString("cover_image_url", ""),
                                        galleryList,
                                        obj.optInt("is_reserved", 0)
                                );

                                houseList.add(house);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No listings found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Network error. Check server.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        if (getActivity() != null) {
            Volley.newRequestQueue(requireContext()).add(request);
        }
    }
}
