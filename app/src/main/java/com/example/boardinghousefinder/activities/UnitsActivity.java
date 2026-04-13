package com.example.boardinghousefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnitsActivity extends AppCompatActivity {

    ImageView btnBackUnits, ivUnitPhoto;
    TextView tvUnitsPropertyName, tvUnitsCounter,
            tvUnitName, tvUnitPrice, tvUnitDeposit,
            tvUnitDescription, tvUnitStatus;
    MaterialButton btnReserveUnit, btnUnitPrev, btnUnitNext;
    boolean isOwner = false;

    private final ArrayList<JSONObject> unitsList = new ArrayList<>();
    private int currentIndex = 0;
    private int boardingId;
    private String propertyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);

        btnBackUnits        = findViewById(R.id.btnBackUnits);
        ivUnitPhoto         = findViewById(R.id.ivUnitPhoto);
        tvUnitsPropertyName = findViewById(R.id.tvUnitsPropertyName);
        tvUnitsCounter      = findViewById(R.id.tvUnitsCounter);
        tvUnitName          = findViewById(R.id.tvUnitName);
        tvUnitPrice         = findViewById(R.id.tvUnitPrice);
        tvUnitDeposit       = findViewById(R.id.tvUnitDeposit);
        tvUnitDescription   = findViewById(R.id.tvUnitDescription);
        tvUnitStatus        = findViewById(R.id.tvUnitStatus);
        btnReserveUnit      = findViewById(R.id.btnReserveUnit);
        btnUnitPrev         = findViewById(R.id.btnUnitPrev);
        btnUnitNext         = findViewById(R.id.btnUnitNext);

        boardingId   = getIntent().getIntExtra("boarding_id", -1);
        propertyName = getIntent().getStringExtra("property_name");
        if (propertyName != null) tvUnitsPropertyName.setText(propertyName);

        btnBackUnits.setOnClickListener(v -> finish());

        btnUnitPrev.setOnClickListener(v -> {
            if (currentIndex > 0) showUnit(--currentIndex);
        });

        btnUnitNext.setOnClickListener(v -> {
            if (currentIndex < unitsList.size() - 1) showUnit(++currentIndex);
        });

        isOwner = "owner".equalsIgnoreCase(SessionManager.getUserRole());

        btnReserveUnit.setOnClickListener(v -> {
            if (!SessionManager.isLoggedIn()) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class).putExtra("openLogin", true));
                return;
            }
            if (isOwner) {
                Toast.makeText(this, "Owners cannot reserve a unit.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject unit  = unitsList.get(currentIndex);
                int    unitId    = unit.getInt("id");
                String unitName  = unit.getString("unit_name");
                String unitPrice = unit.getString("price");

                Intent resIntent = new Intent(this, ReservationActivity.class);
                resIntent.putExtra("boarding_id",    boardingId);
                resIntent.putExtra("unit_id",        unitId);
                resIntent.putExtra("property_name",  propertyName);
                resIntent.putExtra("unit_name",      unitName);
                resIntent.putExtra("property_type",  "Unit");
                resIntent.putExtra("property_price", unitPrice);
                startActivity(resIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Error selecting unit", Toast.LENGTH_SHORT).show();
            }
        });

        if (boardingId != -1) fetchUnits(boardingId);
    }

    private void fetchUnits(int boardingId) {
        String url = "http://192.168.254.104/casptone/fetch_units.php?boarding_id=" + boardingId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray units = response.getJSONArray("units");
                            unitsList.clear();
                            for (int i = 0; i < units.length(); i++) unitsList.add(units.getJSONObject(i));
                            if (!unitsList.isEmpty()) { currentIndex = 0; showUnit(0); }
                            else { Toast.makeText(this, "No units found", Toast.LENGTH_SHORT).show(); finish(); }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not load units", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error loading units", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void showUnit(int index) {
        try {
            JSONObject unit = unitsList.get(index);
            int total = unitsList.size();

            tvUnitsCounter.setText((index + 1) + " of " + total);
            tvUnitName.setText(unit.getString("unit_name"));
            tvUnitPrice.setText("₱" + unit.getString("price") + "/month");
            tvUnitDeposit.setText("Deposit: ₱" + unit.getString("deposit"));
            tvUnitDescription.setText(unit.getString("description"));

            int isReserved = unit.getInt("is_reserved");

            // Handle Reserve Button Visibility
            if (isOwner) {
                btnReserveUnit.setVisibility(View.GONE);
            } else {
                btnReserveUnit.setVisibility(View.VISIBLE);
                if (isReserved == 1) {
                    btnReserveUnit.setEnabled(false);
                    btnReserveUnit.setText("Already Reserved");
                    btnReserveUnit.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
                } else {
                    btnReserveUnit.setEnabled(true);
                    btnReserveUnit.setText("Reserve This Unit");
                    btnReserveUnit.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
                }
            }

            // Handle Status Text
            if (isReserved == 1) {
                tvUnitStatus.setText("🔴 Reserved");
                tvUnitStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvUnitStatus.setText("✅ Available");
                tvUnitStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            String imageUrl = unit.optString("image_url", "");
            if (!imageUrl.isEmpty()) {
                ivUnitPhoto.setVisibility(View.VISIBLE);
                Glide.with(this).load(imageUrl).placeholder(R.drawable.test2).error(R.drawable.test2).centerCrop().into(ivUnitPhoto);
            } else {
                ivUnitPhoto.setVisibility(View.GONE);
            }

            btnUnitPrev.setVisibility(index > 0 ? View.VISIBLE : View.INVISIBLE);
            btnUnitNext.setVisibility(index < total - 1 ? View.VISIBLE : View.INVISIBLE);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading unit details", Toast.LENGTH_SHORT).show();
        }
    }
}