package com.example.boardinghousefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

public class OwnerDetailActivity extends AppCompatActivity {

    private static final int REQUEST_UPDATE = 200;

    ImageView imageDetail, btnBack;
    TextView tvName, tvPrice, tvDescription, tvLocation,
            tvType, tvSpecs, tvServices, tvRules,
            tvContactName, tvContactEmail, tvContactPhone, tvDeposit;
    MaterialButton btnUpdate, btnDelete, btnMap, btnMakeAvailable;
    int boardingId;
    int isReserved; // NEW

    String title, propertyType, price, deposit, description, rules, services;
    String maxGuest, bedrooms, beds, bathrooms;
    String street, city, province, zipCode, country;
    String firstName, lastName, email, phone, coverUrl;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_detail);

        imageDetail       = findViewById(R.id.imageDetail);
        btnBack           = findViewById(R.id.btnBack);
        tvName            = findViewById(R.id.textNameDetail);
        tvPrice           = findViewById(R.id.textPriceDetail);
        tvDescription     = findViewById(R.id.textDescriptionDetail);
        tvLocation        = findViewById(R.id.textLocationDetail);
        tvType            = findViewById(R.id.textTypeDetail);
        tvSpecs           = findViewById(R.id.textSpecsDetail);
        tvServices        = findViewById(R.id.textServicesDetail);
        tvRules           = findViewById(R.id.textRulesDetail);
        tvDeposit         = findViewById(R.id.textDepositDetail);
        tvContactName     = findViewById(R.id.textContactName);
        tvContactEmail    = findViewById(R.id.textContactEmail);
        tvContactPhone    = findViewById(R.id.textContactPhone);
        btnUpdate         = findViewById(R.id.btnUpdate);
        btnDelete         = findViewById(R.id.btnDelete);
        btnMap            = findViewById(R.id.btnMap);
        btnMakeAvailable  = findViewById(R.id.btnMakeAvailable); // NEW

        Intent i     = getIntent();
        boardingId   = i.getIntExtra("id", -1);
        isReserved   = i.getIntExtra("is_reserved", 0); // NEW
        title        = i.getStringExtra("title");
        propertyType = i.getStringExtra("propertyType");
        price        = i.getStringExtra("price");
        deposit      = i.getStringExtra("deposit");
        description  = i.getStringExtra("description");
        rules        = i.getStringExtra("rules");
        services     = i.getStringExtra("services");
        maxGuest     = i.getStringExtra("maxGuest");
        bedrooms     = i.getStringExtra("bedrooms");
        beds         = i.getStringExtra("beds");
        bathrooms    = i.getStringExtra("bathrooms");
        street       = i.getStringExtra("streetAddress");
        city         = i.getStringExtra("city");
        province     = i.getStringExtra("province");
        zipCode      = i.getStringExtra("zipCode");
        country      = i.getStringExtra("country");
        firstName    = i.getStringExtra("firstName");
        lastName     = i.getStringExtra("lastName");
        email        = i.getStringExtra("email");
        phone        = i.getStringExtra("phone");
        coverUrl     = i.getStringExtra("coverImageUrl");
        latitude     = i.getDoubleExtra("latitude", 0.0);
        longitude    = i.getDoubleExtra("longitude", 0.0);

        populateUI();

        // Show "Make Available" only if currently reserved
        if (isReserved == 1) {
            btnMakeAvailable.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> openUpdateActivity());

        btnMakeAvailable.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Make Available")
                        .setMessage("This will reset the listing to available and cancel the current approved reservation. Are you sure?")
                        .setPositiveButton("Yes, Make Available", (dialog, which) -> makePropertyAvailable())
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Listing")
                        .setMessage("Are you sure you want to delete this boarding house?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteListing())
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        btnMap.setOnClickListener(v -> {
            if (latitude == 0.0 && longitude == 0.0) {
                Toast.makeText(this, "No location set.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent mapIntent = new Intent(this, MapViewActivity.class);
            mapIntent.putExtra("latitude",  latitude);
            mapIntent.putExtra("longitude", longitude);
            mapIntent.putExtra("title",     title);
            startActivity(mapIntent);
        });
    }

    private void makePropertyAvailable() {
        String url = "http://192.168.254.104/casptone/make_available.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(this, "Listing is now available", Toast.LENGTH_SHORT).show();
                        btnMakeAvailable.setVisibility(View.GONE);
                        isReserved = 0;
                        setResult(RESULT_OK);
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type",     "property");
                params.put("id",       String.valueOf(boardingId));
                params.put("owner_id", String.valueOf(SessionManager.getUserId()));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void populateUI() {
        tvName.setText(title);
        tvPrice.setText("₱" + price + "/month");
        tvDescription.setText(description);
        tvType.setText(propertyType);
        tvDeposit.setText("Deposit: ₱" + deposit);
        tvLocation.setText(street + ", " + city + ", " + province + ", " + zipCode + ", " + country);
        tvSpecs.setText("👥 " + maxGuest + " guests  •  🛏 " + bedrooms
                + " bedrooms  •  " + beds + " beds  •  🚿 " + bathrooms + " bathrooms");
        tvServices.setText(services != null && !services.isEmpty() ? services : "None listed");
        tvRules.setText("House Rules:\n" + (rules != null && !rules.isEmpty() ? rules : "No rules specified"));
        tvContactName.setText("👤 " + firstName + " " + lastName);
        tvContactEmail.setText("✉ " + email);
        tvContactPhone.setText("📞 " + phone);

        Glide.with(this).load(coverUrl)
                .placeholder(R.drawable.test2).error(R.drawable.test2)
                .centerCrop().into(imageDetail);
    }

    private void openUpdateActivity() {
        Intent intent = new Intent(this, UpdateBoardingActivity.class);
        intent.putExtra("id",            boardingId);
        intent.putExtra("title",         title);
        intent.putExtra("propertyType",  propertyType);
        intent.putExtra("price",         price);
        intent.putExtra("deposit",       deposit);
        intent.putExtra("description",   description);
        intent.putExtra("rules",         rules);
        intent.putExtra("services",      services);
        intent.putExtra("maxGuest",      maxGuest);
        intent.putExtra("bedrooms",      bedrooms);
        intent.putExtra("beds",          beds);
        intent.putExtra("bathrooms",     bathrooms);
        intent.putExtra("streetAddress", street);
        intent.putExtra("city",          city);
        intent.putExtra("province",      province);
        intent.putExtra("zipCode",       zipCode);
        intent.putExtra("country",       country);
        intent.putExtra("firstName",     firstName);
        intent.putExtra("lastName",      lastName);
        intent.putExtra("email",         email);
        intent.putExtra("phone",         phone);
        intent.putExtra("coverImageUrl", coverUrl);
        intent.putExtra("latitude",      latitude);
        intent.putExtra("longitude",     longitude);
        startActivityForResult(intent, REQUEST_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void deleteListing() {
        String url = "http://192.168.254.104/casptone/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(this, "Listing deleted", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
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