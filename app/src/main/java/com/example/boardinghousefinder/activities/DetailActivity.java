package com.example.boardinghousefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    ImageView imageDetail, btnBack, btnFavorite;
    TextView tvName, tvPrice, tvDescription, tvLocation,
            tvType, tvSpecs, tvServices, tvRules,
            tvContactName, tvContactEmail, tvContactPhone, tvDeposit;
    Button btnMap, btnMessage, btnReserve, btnDelete;
    boolean isFavorite = false;
    int boardingId;

    MaterialCardView cardUnitsChip;
    TextView tvUnitsChipLabel;

    private final ArrayList<JSONObject> unitsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageDetail    = findViewById(R.id.imageDetail);
        btnBack        = findViewById(R.id.btnBack);
        btnFavorite    = findViewById(R.id.btnFavorite);
        tvName         = findViewById(R.id.textNameDetail);
        tvPrice        = findViewById(R.id.textPriceDetail);
        tvDescription  = findViewById(R.id.textDescriptionDetail);
        tvLocation     = findViewById(R.id.textLocationDetail);
        tvType         = findViewById(R.id.textTypeDetail);
        tvSpecs        = findViewById(R.id.textSpecsDetail);
        tvServices     = findViewById(R.id.textServicesDetail);
        tvRules        = findViewById(R.id.textRulesDetail);
        tvDeposit      = findViewById(R.id.textDepositDetail);
        tvContactName  = findViewById(R.id.textContactName);
        tvContactEmail = findViewById(R.id.textContactEmail);
        tvContactPhone = findViewById(R.id.textContactPhone);
        btnMap         = findViewById(R.id.btnMap);
        btnMessage     = findViewById(R.id.btnMessage);
        btnReserve     = findViewById(R.id.btnReserve);
        btnDelete      = findViewById(R.id.btnDelete);
        cardUnitsChip    = findViewById(R.id.cardUnitsChip);
        tvUnitsChipLabel = findViewById(R.id.tvUnitsChipLabel);

        Intent i        = getIntent();
        boardingId          = i.getIntExtra("id", -1);
        String title        = i.getStringExtra("title");
        String propertyType = i.getStringExtra("propertyType");
        String price        = i.getStringExtra("price");
        String deposit      = i.getStringExtra("deposit");
        String description  = i.getStringExtra("description");
        String rules        = i.getStringExtra("rules");
        String services     = i.getStringExtra("services");
        String maxGuest     = i.getStringExtra("maxGuest");
        String bedrooms     = i.getStringExtra("bedrooms");
        String beds         = i.getStringExtra("beds");
        String bathrooms    = i.getStringExtra("bathrooms");
        String street       = i.getStringExtra("streetAddress");
        String city         = i.getStringExtra("city");
        String province     = i.getStringExtra("province");
        String zipCode      = i.getStringExtra("zipCode");
        String country      = i.getStringExtra("country");
        String firstName    = i.getStringExtra("firstName");
        String lastName     = i.getStringExtra("lastName");
        String email        = i.getStringExtra("email");
        String phone        = i.getStringExtra("phone");
        String coverUrl     = i.getStringExtra("coverImageUrl");
        Double latitude     = i.getDoubleExtra("latitude", 0.0);
        Double longitude    = i.getDoubleExtra("longitude", 0.0);
        int isReserved      = i.getIntExtra("is_reserved", 0); // Read reservation status
        ArrayList<String> galleryUrls = i.getStringArrayListExtra("galleryImageUrls");

        ArrayList<String> allImages = new ArrayList<>();
        if (coverUrl != null && !coverUrl.isEmpty()) allImages.add(coverUrl);
        if (galleryUrls != null) allImages.addAll(galleryUrls);

        tvName.setText(title);
        tvPrice.setText(price + "/month");
        tvDescription.setText(description);
        tvType.setText(propertyType);
        tvDeposit.setText("Deposit: ₱" + deposit);
        tvLocation.setText(street + ", " + city + ", " + province + ", " + zipCode + ", " + country);
        tvSpecs.setText("👥 " + maxGuest + " guests  •  🛏 " + bedrooms
                + " bedrooms  •  " + beds + " beds  •  🚿 " + bathrooms + " bathrooms");
        tvServices.setText("Amenities: " + (services != null && !services.isEmpty() ? services : "None listed"));
        tvRules.setText("House Rules:\n" + (rules != null && !rules.isEmpty() ? rules : "No rules specified"));
        tvContactName.setText("👤 " + firstName + " " + lastName);
        tvContactEmail.setText("✉ " + email);
        tvContactPhone.setText("📞 " + phone);

        // Show/hide buttons based on role and reservation status
        String role = SessionManager.getUserRole();
        if ("owner".equalsIgnoreCase(role)) {
            btnDelete.setVisibility(View.VISIBLE);
            btnReserve.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.GONE);
            btnReserve.setVisibility(View.VISIBLE);
            if (isReserved == 1) {
                btnReserve.setEnabled(false);
                btnReserve.setText("Already Reserved");
                btnReserve.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#9E9E9E")));
            } else {
                btnReserve.setEnabled(true);
                btnReserve.setText("Reserve Now");
            }
        }

        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.test2)
                .error(R.drawable.test2)
                .centerCrop()
                .into(imageDetail);

        imageDetail.setOnClickListener(v -> {
            if (!allImages.isEmpty()) {
                Intent viewerIntent = new Intent(this, ImageViewerActivity.class);
                viewerIntent.putStringArrayListExtra("images", allImages);
                viewerIntent.putExtra("startIndex", 0);
                startActivity(viewerIntent);
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            btnFavorite.setImageResource(isFavorite
                    ? android.R.drawable.btn_star_big_on
                    : android.R.drawable.btn_star_big_off);
        });

        btnMap.setOnClickListener(v -> {
            if (latitude != 0 && longitude != 0) {
                Intent mapIntent = new Intent(this, MapViewActivity.class);
                mapIntent.putExtra("latitude",  latitude);
                mapIntent.putExtra("longitude", longitude);
                mapIntent.putExtra("title",     title);
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "No map location set for this listing.", Toast.LENGTH_SHORT).show();
            }
        });

        btnMessage.setOnClickListener(v -> {
            if (!SessionManager.isLoggedIn()) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class).putExtra("openLogin", true));
                return;
            }
            Toast.makeText(this, "Message clicked", Toast.LENGTH_SHORT).show();
        });

        // ── Reserve Now ──────────────────────────────────────────────
        btnReserve.setOnClickListener(v -> {
            if (!SessionManager.isLoggedIn()) {
                Toast.makeText(this, "Please login first to reserve.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class).putExtra("openLogin", true));
                return;
            }
            if ("owner".equalsIgnoreCase(SessionManager.getUserRole())) {
                Toast.makeText(this, "Owners cannot reserve a property.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (propertyType != null &&
                    (propertyType.equalsIgnoreCase("Boarding House") ||
                            propertyType.equalsIgnoreCase("Bedspace"))) {
                Toast.makeText(this,
                        "This property type requires direct contact with the owner.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            // Open reservation form
            Intent resIntent = new Intent(this, ReservationActivity.class);
            resIntent.putExtra("boarding_id",    boardingId);
            resIntent.putExtra("property_name",  title);
            resIntent.putExtra("property_type",  propertyType);
            resIntent.putExtra("property_price", price);
            startActivity(resIntent);
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Listing")
                .setMessage("Are you sure you want to delete this boarding house?")
                .setPositiveButton("Delete", (dialog, which) -> deleteListing())
                .setNegativeButton("Cancel", null)
                .show());

        cardUnitsChip.setOnClickListener(v -> {
            Intent unitsIntent = new Intent(this, UnitsActivity.class);
            unitsIntent.putExtra("boarding_id",   boardingId);
            unitsIntent.putExtra("property_name", title);
            startActivity(unitsIntent);
        });

        if (boardingId != -1) fetchUnitsCount(boardingId);
    }

    private void fetchUnitsCount(int boardingId) {
        String url = "http://192.168.254.104/casptone/fetch_units.php?boarding_id=" + boardingId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray units = response.getJSONArray("units");
                            int count = units.length();
                            if (count > 0) {
                                String label = count == 1 ? "1 unit available" : count + " units available";
                                tvUnitsChipLabel.setText(label);
                                cardUnitsChip.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) { }
                },
                error -> { }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void deleteListing() {
        String url = "http://192.168.254.104/casptone/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(this, "Listing deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(boardingId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}