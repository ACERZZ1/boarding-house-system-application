package com.example.boardinghousefinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.activities.BoarderReservationsActivity;
import com.example.boardinghousefinder.activities.OwnerReservationsActivity;
import com.example.boardinghousefinder.activities.OwnerDetailActivity;
import com.example.boardinghousefinder.activities.AddBoardingActivity;
import com.example.boardinghousefinder.adapters.MyListingAdapter;
import com.example.boardinghousefinder.models.BoardingHouse;
import com.example.boardinghousefinder.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountFragment extends Fragment implements MyListingAdapter.OnDeleteClickListener {

    private TextView textName, textRole, textEmail;
    private MaterialButton btnLogin;
    private LinearLayout layoutOptions, layoutOwnerListings, layoutListingsBody;
    private MaterialCardView cardAddListing, cardReservations, cardListingsHeader;
    private TextView btnAddListing, btnReservations, btnLogout;
    private TextView tvListingsCount;
    private ImageView ivChevron;

    private RecyclerView recyclerMyListings;
    private MyListingAdapter adapter;
    private List<BoardingHouse> myHouses;

    private boolean isListingsExpanded = false;

    public AccountFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        textName         = view.findViewById(R.id.textName);
        textRole         = view.findViewById(R.id.textRole);
        textEmail        = view.findViewById(R.id.textEmail);
        btnLogin         = view.findViewById(R.id.btnLogin);
        layoutOptions    = view.findViewById(R.id.layoutOptions);
        cardAddListing   = view.findViewById(R.id.cardAddListing);
        btnAddListing    = view.findViewById(R.id.btnAddListing);
        cardReservations = view.findViewById(R.id.cardReservations);
        btnReservations  = view.findViewById(R.id.btnReservations);
        btnLogout        = view.findViewById(R.id.btnLogout);
        layoutOwnerListings = view.findViewById(R.id.layoutOwnerListings);
        layoutListingsBody  = view.findViewById(R.id.layoutListingsBody);
        recyclerMyListings  = view.findViewById(R.id.recyclerMyListings);
        cardListingsHeader  = view.findViewById(R.id.cardListingsHeader);
        tvListingsCount     = view.findViewById(R.id.tvListingsCount);
        ivChevron           = view.findViewById(R.id.ivChevron);

        myHouses = new ArrayList<>();
        adapter  = new MyListingAdapter(getContext(), myHouses, this);
        recyclerMyListings.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMyListings.setAdapter(adapter);
        recyclerMyListings.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(house -> {
            Intent intent = new Intent(getContext(), OwnerDetailActivity.class);
            intent.putExtra("id",            house.getId());
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
            intent.putExtra("latitude",      house.getLatitude());
            intent.putExtra("longitude",     house.getLongitude());
            intent.putExtra("is_reserved",   house.getIsReserved()); // NEW: pass reservation status
            startActivity(intent);
        });

        updateUI();

        btnLogin.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnLogout.setOnClickListener(v -> {
            SessionManager.logout();
            isListingsExpanded = false;
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        cardListingsHeader.setOnClickListener(v -> toggleListings());

        btnAddListing.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddBoardingActivity.class)));

        // ── Reservations button — role-aware ─────────────────────────
        btnReservations.setOnClickListener(v -> {
            String role = SessionManager.getUserRole();
            if ("owner".equalsIgnoreCase(role)) {
                startActivity(new Intent(getContext(), OwnerReservationsActivity.class));
            } else {
                startActivity(new Intent(getContext(), BoarderReservationsActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (!isAdded()) return;

        if (SessionManager.isLoggedIn()) {
            String role     = SessionManager.getUserRole();
            String username = SessionManager.getUsername();
            String email    = SessionManager.getEmail();

            textName.setText(username.isEmpty() ? "User" : username);
            textEmail.setText(email.isEmpty() ? "" : email);

            boolean isOwner = role.equalsIgnoreCase("owner");
            if (isOwner) {
                textRole.setText("Property Owner");
                cardAddListing.setVisibility(View.VISIBLE);
                layoutOwnerListings.setVisibility(View.VISIBLE);
                // Owner also sees reservations button (for pending requests)
                cardReservations.setVisibility(View.VISIBLE);
                btnReservations.setText("Pending Reservations");
                fetchOwnerListingsFromServer();
            } else {
                textRole.setText("Boarder");
                cardAddListing.setVisibility(View.GONE);
                layoutOwnerListings.setVisibility(View.GONE);
                cardReservations.setVisibility(View.VISIBLE);
                btnReservations.setText("My Reservations");
            }
            btnLogin.setVisibility(View.GONE);
            layoutOptions.setVisibility(View.VISIBLE);
        } else {
            textName.setText("Guest User");
            textRole.setText("Not logged in");
            textEmail.setText("");
            btnLogin.setVisibility(View.VISIBLE);
            layoutOptions.setVisibility(View.GONE);
            layoutOwnerListings.setVisibility(View.GONE);
        }
    }

    private void toggleListings() {
        isListingsExpanded = !isListingsExpanded;
        layoutListingsBody.setVisibility(isListingsExpanded ? View.VISIBLE : View.GONE);
        ivChevron.setRotation(isListingsExpanded ? 180f : 0f);
    }

    private void fetchOwnerListingsFromServer() {
        int ownerId = SessionManager.getUserId();
        if (ownerId <= 0) return;

        String url = "http://192.168.254.104/casptone/fetch_my_listing.php?owner_id=" + ownerId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded() || getContext() == null) return;
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            myHouses.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                BoardingHouse house = new BoardingHouse(
                                        obj.getInt("id"),
                                        obj.optString("title", "Untitled"),
                                        obj.optString("property_type", ""),
                                        obj.optString("price", "0"),
                                        obj.optString("deposit", ""),
                                        obj.optString("description", ""),
                                        obj.optString("rules", ""),
                                        obj.optString("services", ""),
                                        obj.optString("max_guest", ""),
                                        obj.optString("bedrooms", ""),
                                        obj.optString("beds", ""),
                                        obj.optString("bathrooms", ""),
                                        obj.optString("street_address", ""),
                                        obj.optString("city", ""),
                                        obj.optString("province", ""),
                                        obj.optString("zip_code", ""),
                                        obj.optString("country", ""),
                                        obj.optString("first_name", ""),
                                        obj.optString("last_name", ""),
                                        obj.optString("email", ""),
                                        obj.optString("phone", ""),
                                        obj.optDouble("latitude", 0),
                                        obj.optDouble("longitude", 0),
                                        obj.optString("cover_image_url", ""),
                                        null,
                                        obj.optInt("is_reserved", 0) // NEW: parse from JSON
                                );
                                myHouses.add(house);
                            }
                            adapter.notifyDataSetChanged();
                            tvListingsCount.setText(String.valueOf(myHouses.size()));
                        }
                    } catch (Exception e) {
                        Log.e("FETCH_DEBUG", "Error: " + e.getMessage());
                    }
                },
                error -> Log.e("FETCH_DEBUG", "Volley Error: " + error.toString())
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }

    @Override
    public void onDeleteClick(int propertyId, int position) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Delete Property")
                .setMessage("Delete this listing?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProperty(propertyId, position))
                .setNegativeButton("Cancel", null).show();
    }

    private void deleteProperty(int id, int position) {
        String url = "http://192.168.254.104/casptone/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        myHouses.remove(position);
                        adapter.notifyItemRemoved(position);
                        tvListingsCount.setText(String.valueOf(myHouses.size()));
                    }
                }, null
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id", String.valueOf(id));
                return p;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }
}