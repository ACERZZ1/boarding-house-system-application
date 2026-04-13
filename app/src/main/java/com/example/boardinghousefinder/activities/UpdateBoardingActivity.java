package com.example.boardinghousefinder.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateBoardingActivity extends AppCompatActivity {

    View step1, step2, step3, step4, step5, step6, step7;
    View defaultButtonBar, unitButtonBar;
    int currentStep = 1;
    int boardingId;

    Spinner spinnerPropertyType;
    EditText etTitle, etMaxGuests, etBedrooms, etBeds, etBathrooms, etDescription;
    EditText etStreet, etCity, etProvince, etZip, etCountry;
    EditText etPrice, etDeposit, etRules;
    EditText etFirstName, etLastName, etEmail, etPhone;
    CheckBox cbWifi, cbParking, cbAircon, cbLaundry, cbCCTV, cbWater, cbElectricity;

    private static final int PICK_COVER_IMAGE    = 101;
    private static final int PICK_GALLERY_IMAGES = 102;
    private static final int PICK_LOCATION       = 103;
    Uri coverImageUri, galleryImageUri;
    Button btnCoverImage, btnGalleryImages;
    private TextView tvLocationStatus;
    double latitude, longitude;

    // Interface for API 23 compatibility (replacing Consumer)
    interface StringConsumer {
        void accept(String s);
    }

    // ── Units ─────────────────────────────────────────────────────────────────
    LinearLayout llUnitsContainer;
    private static final int UNIT_IMAGE_BASE = 200; 

    private static class UnitRow {
        int     unitId;      
        String  unitName, price, deposit, description;
        boolean isReserved;
        Uri     imageUri;    
        int     pickRequestCode;
        View    cardView;

        UnitRow(int unitId, String unitName, String price,
                String deposit, String description, boolean isReserved) {
            this.unitId      = unitId;
            this.unitName    = unitName;
            this.price       = price;
            this.deposit     = deposit;
            this.description = description;
            this.isReserved  = isReserved;
        }
    }

    private final List<UnitRow> unitRows       = new ArrayList<>();
    private final List<Integer> deletedUnitIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boarding); 

        // ── Steps ─────────────────────────────────────────────────────────────
        step1 = findViewById(R.id.step1); step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3); step4 = findViewById(R.id.step4);
        step5 = findViewById(R.id.step5); step6 = findViewById(R.id.step6);
        step7 = findViewById(R.id.step7);
        defaultButtonBar = findViewById(R.id.defaultButtonBar);
        unitButtonBar    = findViewById(R.id.unitButtonBar);

        // ── Inputs ────────────────────────────────────────────────────────────
        etTitle             = findViewById(R.id.etPropertyTitle);
        spinnerPropertyType = findViewById(R.id.spinnerPropertyType);
        etMaxGuests         = findViewById(R.id.etMaxGuests);
        etBedrooms          = findViewById(R.id.etBedrooms);
        etBeds              = findViewById(R.id.etBeds);
        etBathrooms         = findViewById(R.id.etBathrooms);
        etDescription       = findViewById(R.id.etDescription);
        etStreet            = findViewById(R.id.etStreetAddress);
        etCity              = findViewById(R.id.etCity);
        etProvince          = findViewById(R.id.etProvince);
        etZip               = findViewById(R.id.etZipCode);
        etCountry           = findViewById(R.id.etCountry);
        etPrice             = findViewById(R.id.etPrice);
        etDeposit           = findViewById(R.id.etDeposit);
        etRules             = findViewById(R.id.etRules);
        etFirstName         = findViewById(R.id.etFirstName);
        etLastName          = findViewById(R.id.etLastName);
        etEmail             = findViewById(R.id.etContactEmail);
        etPhone             = findViewById(R.id.etPhoneNumber);
        cbWifi        = findViewById(R.id.cbWifi);
        cbParking     = findViewById(R.id.cbParking);
        cbAircon      = findViewById(R.id.cbAircon);
        cbLaundry     = findViewById(R.id.cbLaundry);
        cbCCTV        = findViewById(R.id.cbCCTV);
        cbWater       = findViewById(R.id.cbWater);
        cbElectricity = findViewById(R.id.cbElectricity);
        llUnitsContainer = findViewById(R.id.llUnitsContainer);

        // ── Spinner ───────────────────────────────────────────────────────────
        String[] types = {"Apartment", "Boarding House", "Dormitory", "Studio", "Bedspace"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerPropertyType.setAdapter(spinnerAdapter);

        // ── Pre-fill from intent ──────────────────────────
        Intent i   = getIntent();
        boardingId = i.getIntExtra("id", -1);
        etTitle.setText(i.getStringExtra("title"));
        etMaxGuests.setText(i.getStringExtra("maxGuest"));
        etBedrooms.setText(i.getStringExtra("bedrooms"));
        etBeds.setText(i.getStringExtra("beds"));
        etBathrooms.setText(i.getStringExtra("bathrooms"));
        etDescription.setText(i.getStringExtra("description"));
        etStreet.setText(i.getStringExtra("streetAddress"));
        etCity.setText(i.getStringExtra("city"));
        etProvince.setText(i.getStringExtra("province"));
        etZip.setText(i.getStringExtra("zipCode"));
        etCountry.setText(i.getStringExtra("country"));
        etPrice.setText(i.getStringExtra("price"));
        etDeposit.setText(i.getStringExtra("deposit"));
        etRules.setText(i.getStringExtra("rules"));
        etFirstName.setText(i.getStringExtra("firstName"));
        etLastName.setText(i.getStringExtra("lastName"));
        etEmail.setText(i.getStringExtra("email"));
        etPhone.setText(i.getStringExtra("phone"));
        latitude  = i.getDoubleExtra("latitude",  0.0);
        longitude = i.getDoubleExtra("longitude", 0.0);

        String propertyType = i.getStringExtra("propertyType");
        if (propertyType != null) {
            List<String> typeList = Arrays.asList(types);
            int idx = typeList.indexOf(propertyType);
            if (idx >= 0) spinnerPropertyType.setSelection(idx);
        }

        String services = i.getStringExtra("services");
        if (services != null) {
            cbWifi.setChecked(services.contains("WiFi"));
            cbParking.setChecked(services.contains("Parking"));
            cbAircon.setChecked(services.contains("Aircon"));
            cbLaundry.setChecked(services.contains("Laundry"));
            cbCCTV.setChecked(services.contains("CCTV"));
            cbWater.setChecked(services.contains("Water"));
            cbElectricity.setChecked(services.contains("Electricity"));
        }

        showStep(1);

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (currentStep < 7) { currentStep++; showStep(currentStep); }
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (currentStep > 1) { currentStep--; showStep(currentStep); }
            else finish();
        });

        findViewById(R.id.btnBackFromUnits).setOnClickListener(v -> {
            currentStep = 6; showStep(currentStep);
        });
        findViewById(R.id.btnAddUnit).setOnClickListener(v -> addBlankUnitCard());
        findViewById(R.id.btnPost).setOnClickListener(v -> sendUpdateToServer());

        btnCoverImage    = findViewById(R.id.btnCoverImage);
        btnGalleryImages = findViewById(R.id.btnGalleryImages);
        btnCoverImage.setOnClickListener(v -> openGallery(PICK_COVER_IMAGE));
        btnGalleryImages.setOnClickListener(v -> openGallery(PICK_GALLERY_IMAGES));

        Button btnPickLocation = findViewById(R.id.btnPickLocation);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        if (latitude != 0.0 || longitude != 0.0) {
            btnPickLocation.setText("📍 Location Pinned!");
            tvLocationStatus.setVisibility(View.VISIBLE);
            tvLocationStatus.setText("Lat: " + latitude + "  |  Lng: " + longitude);
        }
        btnPickLocation.setOnClickListener(v ->
                startActivityForResult(new Intent(this, MapPickerActivity.class), PICK_LOCATION));

        if (boardingId > 0) fetchExistingUnits();
    }

    private void showStep(int step) {
        step1.setVisibility(View.GONE); step2.setVisibility(View.GONE);
        step3.setVisibility(View.GONE); step4.setVisibility(View.GONE);
        step5.setVisibility(View.GONE); step6.setVisibility(View.GONE);
        step7.setVisibility(View.GONE);

        boolean onStep7 = (step == 7);
        defaultButtonBar.setVisibility(onStep7 ? View.GONE    : View.VISIBLE);
        unitButtonBar.setVisibility(   onStep7 ? View.VISIBLE : View.GONE);

        switch (step) {
            case 1: step1.setVisibility(View.VISIBLE); break;
            case 2: step2.setVisibility(View.VISIBLE); break;
            case 3: step3.setVisibility(View.VISIBLE); break;
            case 4: step4.setVisibility(View.VISIBLE); break;
            case 5: step5.setVisibility(View.VISIBLE); break;
            case 6: step6.setVisibility(View.VISIBLE); break;
            case 7: step7.setVisibility(View.VISIBLE); break;
        }
    }

    private void fetchExistingUnits() {
        String url = "http://192.168.254.104/casptone/fetch_units_for_edit.php?boarding_id=" + boardingId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray units = response.getJSONArray("units");
                            for (int n = 0; n < units.length(); n++) {
                                JSONObject u = units.getJSONObject(n);
                                UnitRow row = new UnitRow(
                                        u.getInt("id"),
                                        u.getString("unit_name"),
                                        u.getString("price"),
                                        u.getString("deposit"),
                                        u.getString("description"),
                                        u.getInt("is_reserved") == 1
                                );
                                row.pickRequestCode = UNIT_IMAGE_BASE + unitRows.size();
                                unitRows.add(row);
                                inflateUnitCard(row);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("UPDATE_UNITS", "fetchExistingUnits: " + e.getMessage());
                    }
                },
                error -> Log.e("UPDATE_UNITS", "Network error fetching units: " + error.toString())
        );
        Volley.newRequestQueue(this).add(req);
    }

    private void inflateUnitCard(UnitRow row) {
        View card = LayoutInflater.from(this)
                .inflate(R.layout.item_unit_input, llUnitsContainer, false);

        EditText etName  = card.findViewById(R.id.etUnitName);
        EditText etPrice = card.findViewById(R.id.etUnitPrice);
        EditText etDep   = card.findViewById(R.id.etUnitDeposit);
        EditText etDesc  = card.findViewById(R.id.etUnitDescription);
        Button   btnPick = card.findViewById(R.id.btnPickUnitImage); 
        Button   btnDel  = card.findViewById(R.id.btnRemoveUnit);

        etName.setText(row.unitName);
        etPrice.setText(row.price);
        etDep.setText(row.deposit);
        etDesc.setText(row.description);

        if (row.isReserved) {
            card.setAlpha(0.55f);
            etName.setEnabled(false);
            etPrice.setEnabled(false);
            etDep.setEnabled(false);
            etDesc.setEnabled(false);
            if (btnPick != null) btnPick.setEnabled(false);
            btnDel.setEnabled(false);
            btnDel.setText("Reserved");

            // Add "Make Available" button dynamically
            com.google.android.material.button.MaterialButton btnMakeAvail =
                    new com.google.android.material.button.MaterialButton(this);
            btnMakeAvail.setText("Make Available");
            btnMakeAvail.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
            btnMakeAvail.setTextColor(android.graphics.Color.WHITE);
            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 16, 0, 0);
            btnMakeAvail.setLayoutParams(lp);

            btnMakeAvail.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Make Unit Available")
                            .setMessage("Reset \"" + row.unitName + "\" to available and cancel its approved reservation?")
                            .setPositiveButton("Yes", (d, which) -> makeUnitAvailable(row, card, btnMakeAvail))
                            .setNegativeButton("Cancel", null)
                            .show()
            );

            // Add it to the card's root LinearLayout
            if (card instanceof android.widget.LinearLayout) {
                ((android.widget.LinearLayout) card).addView(btnMakeAvail);
            } else {
                // item_unit_input root might be a LinearLayout inside a CardView
                android.view.ViewGroup root = (android.view.ViewGroup) card;
                if (root.getChildAt(0) instanceof android.widget.LinearLayout) {
                    ((android.widget.LinearLayout) root.getChildAt(0)).addView(btnMakeAvail);
                }
            }
        }

        etName.addTextChangedListener(w(s  -> row.unitName    = s));
        etPrice.addTextChangedListener(w(s -> row.price       = s));
        etDep.addTextChangedListener(w(s   -> row.deposit     = s));
        etDesc.addTextChangedListener(w(s  -> row.description = s));

        if (btnPick != null) {
            btnPick.setOnClickListener(v -> openGallery(row.pickRequestCode));
        }

        btnDel.setOnClickListener(v -> {
            if (row.isReserved) {
                Toast.makeText(this, "Cannot remove a reserved unit.", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Remove Unit")
                    .setMessage("Remove \"" + row.unitName + "\"?")
                    .setPositiveButton("Remove", (d, which) -> {
                        if (row.unitId > 0) deletedUnitIds.add(row.unitId);
                        unitRows.remove(row);
                        llUnitsContainer.removeView(card);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        row.cardView = card;
        llUnitsContainer.addView(card);
    }

    private void addBlankUnitCard() {
        UnitRow row = new UnitRow(0, "", "", "", "", false);
        row.pickRequestCode = UNIT_IMAGE_BASE + unitRows.size();
        unitRows.add(row);
        inflateUnitCard(row);
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == PICK_COVER_IMAGE) {
            coverImageUri = data.getData();
            btnCoverImage.setText("Cover Selected ✓");

        } else if (requestCode == PICK_GALLERY_IMAGES) {
            galleryImageUri = data.getData();
            btnGalleryImages.setText("Gallery Selected ✓");

        } else if (requestCode == PICK_LOCATION) {
            String lat = data.getStringExtra("latitude");
            String lng = data.getStringExtra("longitude");
            if (lat != null && lng != null) {
                latitude  = Double.parseDouble(lat);
                longitude = Double.parseDouble(lng);
                Button btnPickLocation = findViewById(R.id.btnPickLocation);
                btnPickLocation.setText("📍 Location Updated!");
                tvLocationStatus.setVisibility(View.VISIBLE);
                tvLocationStatus.setText("Lat: " + lat + "  |  Lng: " + lng);
            }

        } else if (requestCode >= UNIT_IMAGE_BASE) {
            for (UnitRow row : unitRows) {
                if (row.pickRequestCode == requestCode) {
                    row.imageUri = data.getData();
                    if (row.cardView != null) {
                        Button btnPick = row.cardView.findViewById(R.id.btnPickUnitImage);
                        if (btnPick != null) btnPick.setText("Image Selected ✓");
                    }
                    break;
                }
            }
        }
    }

    private String getSelectedServices() {
        StringBuilder s = new StringBuilder();
        if (cbWifi.isChecked())        s.append("WiFi,");
        if (cbParking.isChecked())     s.append("Parking,");
        if (cbAircon.isChecked())      s.append("Aircon,");
        if (cbLaundry.isChecked())     s.append("Laundry,");
        if (cbCCTV.isChecked())        s.append("CCTV,");
        if (cbWater.isChecked())       s.append("Water,");
        if (cbElectricity.isChecked()) s.append("Electricity,");
        return s.length() > 0 ? s.substring(0, s.length() - 1) : "";
    }

    private void sendUpdateToServer() {
        String url = "http://192.168.254.104/casptone/update.php";

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                null,
                response -> {
                    String result = new String(response.data);
                    Log.d("UPDATE_BOARDING", "Response: " + result);
                    if (result.contains("success")) {
                        Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Server error: " + result, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network Error. Check connection.", Toast.LENGTH_SHORT).show();
                    Log.e("UPDATE_BOARDING", "Volley Error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id",             String.valueOf(boardingId));
                p.put("title",          etTitle.getText().toString());
                p.put("property_type",  spinnerPropertyType.getSelectedItem().toString());
                p.put("max_guest",      etMaxGuests.getText().toString());
                p.put("bedrooms",       etBedrooms.getText().toString());
                p.put("beds",           etBeds.getText().toString());
                p.put("bathrooms",      etBathrooms.getText().toString());
                p.put("description",    etDescription.getText().toString());
                p.put("street_address", etStreet.getText().toString());
                p.put("city",           etCity.getText().toString());
                p.put("province",       etProvince.getText().toString());
                p.put("zip_code",       etZip.getText().toString());
                p.put("country",        etCountry.getText().toString());
                p.put("services",       getSelectedServices());
                p.put("price",          etPrice.getText().toString());
                p.put("deposit",        etDeposit.getText().toString());
                p.put("rules",          etRules.getText().toString());
                p.put("first_name",     etFirstName.getText().toString());
                p.put("last_name",      etLastName.getText().toString());
                p.put("email",          etEmail.getText().toString());
                p.put("phone",          etPhone.getText().toString());
                p.put("latitude",       String.valueOf(latitude));
                p.put("longitude",      String.valueOf(longitude));

                p.put("unit_count", String.valueOf(unitRows.size()));
                for (int n = 0; n < unitRows.size(); n++) {
                    UnitRow r = unitRows.get(n);
                    p.put("unit_id_"          + n, String.valueOf(r.unitId));
                    p.put("unit_name_"        + n, r.unitName    != null ? r.unitName    : "");
                    p.put("unit_price_"       + n, r.price       != null ? r.price       : "0");
                    p.put("unit_deposit_"     + n, r.deposit     != null ? r.deposit     : "0");
                    p.put("unit_description_" + n, r.description != null ? r.description : "");
                }

                StringBuilder sb = new StringBuilder();
                for (int did : deletedUnitIds) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(did);
                }
                p.put("deleted_unit_ids", sb.toString());
                return p;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> d = new HashMap<>();
                if (coverImageUri != null) {
                    byte[] data = getFileData(coverImageUri);
                    if (data != null)
                        d.put("cover_image", new DataPart("cover.jpg", data, "image/jpeg"));
                }
                if (galleryImageUri != null) {
                    byte[] data = getFileData(galleryImageUri);
                    if (data != null)
                        d.put("gallery_image", new DataPart("gallery.jpg", data, "image/jpeg"));
                }
                for (int n = 0; n < unitRows.size(); n++) {
                    UnitRow r = unitRows.get(n);
                    if (r.imageUri != null) {
                        byte[] data = getFileData(r.imageUri);
                        if (data != null)
                            d.put("unit_image_" + n, new DataPart("unit_" + n + ".jpg", data, "image/jpeg"));
                    }
                }
                return d;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private byte[] getFileData(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(is);
            if (b == null) return null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            return bos.toByteArray();
        } catch (Exception e) {
            Log.e("UPDATE_BOARDING", "getFileData: " + e.getMessage());
            return null;
        }
    }

    private android.text.TextWatcher w(final StringConsumer fn) {
        return new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {}
            public void afterTextChanged(android.text.Editable s) { fn.accept(s.toString()); }
        };
    }
    private void makeUnitAvailable(UnitRow row, View card, com.google.android.material.button.MaterialButton btnMakeAvail) {
        String url = "http://192.168.254.104/casptone/make_available.php";
        com.android.volley.toolbox.StringRequest request =
                new com.android.volley.toolbox.StringRequest(Request.Method.POST, url,
                        response -> {
                            if (response.contains("success")) {
                                Toast.makeText(this, "Unit is now available", Toast.LENGTH_SHORT).show();
                                // Re-enable the card
                                row.isReserved = false;
                                card.setAlpha(1.0f);
                                card.findViewById(R.id.etUnitName).setEnabled(true);
                                card.findViewById(R.id.etUnitPrice).setEnabled(true);
                                card.findViewById(R.id.etUnitDeposit).setEnabled(true);
                                card.findViewById(R.id.etUnitDescription).setEnabled(true);
                                Button btnPick = card.findViewById(R.id.btnPickUnitImage);
                                if (btnPick != null) btnPick.setEnabled(true);
                                Button btnDel = card.findViewById(R.id.btnRemoveUnit);
                                btnDel.setEnabled(true);
                                btnDel.setText("Remove");
                                btnMakeAvail.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(this, "Error: " + response, Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("type",     "unit");
                        params.put("id",       String.valueOf(row.unitId));
                        params.put("owner_id", String.valueOf(com.example.boardinghousefinder.utils.SessionManager.getUserId()));
                        return params;
                    }
                };
        Volley.newRequestQueue(this).add(request);
    }
}