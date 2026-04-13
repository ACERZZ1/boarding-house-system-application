package com.example.boardinghousefinder.activities;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.VolleyMultipartRequest;
import com.example.boardinghousefinder.models.PropertyUnit;
import com.example.boardinghousefinder.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddBoardingActivity extends AppCompatActivity {

    // ── Steps ────────────────────────────────────────────────────────
    View step1, step2, step3, step4, step5, step6, step7;
    int currentStep = 1;

    // ── Step 1 fields ────────────────────────────────────────────────
    Spinner spinnerPropertyType;
    EditText etTitle, etMaxGuests, etBedrooms, etBeds, etBathrooms, etDescription;

    // ── Step 2 fields ────────────────────────────────────────────────
    EditText etStreet, etCity, etProvince, etZip, etCountry;

    // ── Step 3 checkboxes ────────────────────────────────────────────
    CheckBox cbWifi, cbParking, cbAircon, cbLaundry, cbCCTV, cbWater, cbElectricity;

    // ── Step 4 fields ────────────────────────────────────────────────
    EditText etPrice, etDeposit, etRules;

    // ── Step 5 images ────────────────────────────────────────────────
    private static final int PICK_COVER_IMAGE    = 101;
    private static final int PICK_GALLERY_IMAGES = 102;
    private static final int PICK_LOCATION       = 103;
    private static final int PICK_UNIT_IMAGE     = 104;

    Uri coverImageUri, galleryImageUri;
    Button btnCoverImage, btnGalleryImages;

    // ── Step 6 map ───────────────────────────────────────────────────
    private String pickedLatitude  = "";
    private String pickedLongitude = "";
    private Button btnPickLocation;
    private TextView tvLocationStatus;

    // ── Step 7 contact ───────────────────────────────────────────────
    EditText etFirstName, etLastName, etEmail, etPhone;

    // ── Units list ───────────────────────────────────────────────────
    private final ArrayList<PropertyUnit> unitList = new ArrayList<>();
    private LinearLayout llUnitsContainer;          // cards appear here
    private MaterialButton btnAddUnit, btnPost;
    private LinearLayout unitButtonBar;             // "Add Unit" + "Post" row
    private LinearLayout defaultButtonBar;          // "Back" + "Next" row

    // Unit dialog state
    private Uri pendingUnitPhotoUri = null;
    private Button dialogPhotoBtn   = null;         // reference so we can update its text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boarding);

        // Bind steps
        step1 = findViewById(R.id.step1); step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3); step4 = findViewById(R.id.step4);
        step5 = findViewById(R.id.step5); step6 = findViewById(R.id.step6);
        step7 = findViewById(R.id.step7);

        // Bind step 1
        etTitle             = findViewById(R.id.etPropertyTitle);
        spinnerPropertyType = findViewById(R.id.spinnerPropertyType);
        etMaxGuests         = findViewById(R.id.etMaxGuests);
        etBedrooms          = findViewById(R.id.etBedrooms);
        etBeds              = findViewById(R.id.etBeds);
        etBathrooms         = findViewById(R.id.etBathrooms);
        etDescription       = findViewById(R.id.etDescription);

        // Bind step 2
        etStreet   = findViewById(R.id.etStreetAddress);
        etCity     = findViewById(R.id.etCity);
        etProvince = findViewById(R.id.etProvince);
        etZip      = findViewById(R.id.etZipCode);
        etCountry  = findViewById(R.id.etCountry);

        // Bind step 3
        cbWifi        = findViewById(R.id.cbWifi);
        cbParking     = findViewById(R.id.cbParking);
        cbAircon      = findViewById(R.id.cbAircon);
        cbLaundry     = findViewById(R.id.cbLaundry);
        cbCCTV        = findViewById(R.id.cbCCTV);
        cbWater       = findViewById(R.id.cbWater);
        cbElectricity = findViewById(R.id.cbElectricity);

        // Bind step 4
        etPrice   = findViewById(R.id.etPrice);
        etDeposit = findViewById(R.id.etDeposit);
        etRules   = findViewById(R.id.etRules);

        // Bind step 5
        btnCoverImage    = findViewById(R.id.btnCoverImage);
        btnGalleryImages = findViewById(R.id.btnGalleryImages);
        btnCoverImage.setOnClickListener(v -> openGallery(PICK_COVER_IMAGE));
        btnGalleryImages.setOnClickListener(v -> openGallery(PICK_GALLERY_IMAGES));

        // Bind step 6
        btnPickLocation  = findViewById(R.id.btnPickLocation);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        btnPickLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            startActivityForResult(intent, PICK_LOCATION);
        });

        // Bind step 7
        etFirstName = findViewById(R.id.etFirstName);
        etLastName  = findViewById(R.id.etLastName);
        etEmail     = findViewById(R.id.etContactEmail);
        etPhone     = findViewById(R.id.etPhoneNumber);

        // Bind unit section (lives inside step7 layout)
        llUnitsContainer = findViewById(R.id.llUnitsContainer);
        btnAddUnit       = findViewById(R.id.btnAddUnit);
        btnPost          = findViewById(R.id.btnPost);
        unitButtonBar    = findViewById(R.id.unitButtonBar);
        defaultButtonBar = findViewById(R.id.defaultButtonBar);

        // Spinner
        String[] types = {"Apartment", "Boarding House", "Dormitory", "Studio", "Bedspace"};
        spinnerPropertyType.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, types));

        showStep(1);

        // Default nav buttons
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (currentStep < 7) {
                currentStep++;
                showStep(currentStep);
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (currentStep > 1) { currentStep--; showStep(currentStep); }
            else { finish(); }
        });

        // Unit section buttons (only visible on step 7)
        btnAddUnit.setOnClickListener(v -> showAddUnitDialog());
        btnPost.setOnClickListener(v -> sendDataToServer());
        findViewById(R.id.btnBackFromUnits).setOnClickListener(v -> {
                    currentStep--;
                    showStep(currentStep);
        });
    }

    // ── Show / hide steps ────────────────────────────────────────────
    private void showStep(int step) {
        step1.setVisibility(View.GONE); step2.setVisibility(View.GONE);
        step3.setVisibility(View.GONE); step4.setVisibility(View.GONE);
        step5.setVisibility(View.GONE); step6.setVisibility(View.GONE);
        step7.setVisibility(View.GONE);

        switch (step) {
            case 1: step1.setVisibility(View.VISIBLE); break;
            case 2: step2.setVisibility(View.VISIBLE); break;
            case 3: step3.setVisibility(View.VISIBLE); break;
            case 4: step4.setVisibility(View.VISIBLE); break;
            case 5: step5.setVisibility(View.VISIBLE); break;
            case 6: step6.setVisibility(View.VISIBLE); break;
            case 7: step7.setVisibility(View.VISIBLE); break;
        }

        // On step 7: swap button bars
        if (step == 7) {
            defaultButtonBar.setVisibility(View.GONE);
            unitButtonBar.setVisibility(View.VISIBLE);
        } else {
            defaultButtonBar.setVisibility(View.VISIBLE);
            unitButtonBar.setVisibility(View.GONE);
        }
    }

    // ── Add Unit dialog ──────────────────────────────────────────────
    private void showAddUnitDialog() {
        pendingUnitPhotoUri = null;

        // Inflate dialog view
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_unit, null);

        EditText etUnitName    = dialogView.findViewById(R.id.etUnitName);
        EditText etUnitPrice   = dialogView.findViewById(R.id.etUnitPrice);
        EditText etUnitDeposit = dialogView.findViewById(R.id.etUnitDeposit);
        EditText etUnitDesc    = dialogView.findViewById(R.id.etUnitDescription);
        dialogPhotoBtn         = dialogView.findViewById(R.id.btnUnitPhoto);

        dialogPhotoBtn.setOnClickListener(v -> openGallery(PICK_UNIT_IMAGE));

        new AlertDialog.Builder(this)
                .setTitle("Add Unit / Room")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name    = etUnitName.getText().toString().trim();
                    String price   = etUnitPrice.getText().toString().trim();
                    String deposit = etUnitDeposit.getText().toString().trim();
                    String desc    = etUnitDesc.getText().toString().trim();

                    if (name.isEmpty() || price.isEmpty()) {
                        Toast.makeText(this, "Unit name and price are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PropertyUnit unit = new PropertyUnit(name, price, deposit, desc, pendingUnitPhotoUri);
                    unitList.add(unit);
                    addUnitCard(unit);
                    Toast.makeText(this, "Unit \"" + name + "\" added!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Add a card to the units list on screen ───────────────────────
    private void addUnitCard(PropertyUnit unit) {
        View card = LayoutInflater.from(this)
                .inflate(R.layout.item_unit_card, llUnitsContainer, false);

        TextView tvCardName  = card.findViewById(R.id.tvUnitCardName);
        TextView tvCardPrice = card.findViewById(R.id.tvUnitCardPrice);
        ImageView ivCardPhoto = card.findViewById(R.id.ivUnitCardPhoto);
        Button btnRemove     = card.findViewById(R.id.btnRemoveUnit);

        tvCardName.setText("🏠 " + unit.getUnitName());
        tvCardPrice.setText("₱" + unit.getPrice() + "/month"
                + (unit.getDeposit().isEmpty() ? "" : "  •  Deposit: ₱" + unit.getDeposit()));

        if (unit.getPhotoUri() != null) {
            ivCardPhoto.setVisibility(View.VISIBLE);
            ivCardPhoto.setImageURI(unit.getPhotoUri());
        }

        btnRemove.setOnClickListener(v -> {
            unitList.remove(unit);
            llUnitsContainer.removeView(card);
        });

        llUnitsContainer.addView(card);
    }

    // ── Gallery opener ───────────────────────────────────────────────
    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        switch (requestCode) {
            case PICK_COVER_IMAGE:
                coverImageUri = data.getData();
                btnCoverImage.setText("✅ Cover Selected");
                break;
            case PICK_GALLERY_IMAGES:
                galleryImageUri = data.getData();
                btnGalleryImages.setText("✅ Gallery Selected");
                break;
            case PICK_LOCATION:
                pickedLatitude  = data.getStringExtra("latitude");
                pickedLongitude = data.getStringExtra("longitude");
                btnPickLocation.setText("📍 Location Pinned!");
                tvLocationStatus.setVisibility(View.VISIBLE);
                tvLocationStatus.setText("Lat: " + pickedLatitude + "  |  Lng: " + pickedLongitude);
                break;
            case PICK_UNIT_IMAGE:
                pendingUnitPhotoUri = data.getData();
                if (dialogPhotoBtn != null) dialogPhotoBtn.setText("✅ Photo Selected");
                break;
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────
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

    private byte[] getFileData(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(is);
            if (b == null) return null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            return bos.toByteArray();
        } catch (Exception e) { return null; }
    }

    // ── Submit everything to server ──────────────────────────────────
    private void sendDataToServer() {
        if (etFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in contact details first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build units JSON array to send as a single param
        JSONArray unitsJson = new JSONArray();
        for (PropertyUnit u : unitList) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("unit_name",   u.getUnitName());
                obj.put("price",       u.getPrice());
                obj.put("deposit",     u.getDeposit());
                obj.put("description", u.getDescription());
                unitsJson.put(obj);
            } catch (Exception e) { Log.e("UNITS_JSON", e.toString()); }
        }
        final String unitsJsonStr = unitsJson.toString();

        String url = "http://192.168.254.104/casptone/create.php";

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                null,
                response -> {
                    String result = new String(response.data);
                    Log.d("ADD_BOARDING", "Server Response: " + result);
                    if (result.contains("success")) {
                        int count = unitList.size();
                        String msg = count > 0
                                ? "Posted! " + count + " unit(s) added."
                                : "Success! Boarding House Added.";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Server error: " + result, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network Error. Check connection.", Toast.LENGTH_SHORT).show();
                    Log.e("ADD_BOARDING", "Volley Error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("owner_id",       String.valueOf(SessionManager.getUserId()));
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
                p.put("latitude",       pickedLatitude);
                p.put("longitude",      pickedLongitude);
                p.put("units",          unitsJsonStr);   // ← NEW: all units as JSON
                return p;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> d = new HashMap<>();
                if (coverImageUri != null) {
                    d.put("cover_image", new VolleyMultipartRequest.DataPart(
                            "cover.jpg", getFileData(coverImageUri), "image/jpeg"));
                }
                if (galleryImageUri != null) {
                    d.put("gallery_image", new VolleyMultipartRequest.DataPart(
                            "gallery.jpg", getFileData(galleryImageUri), "image/jpeg"));
                }
                // Unit photos: sent as unit_photo_0, unit_photo_1, etc.
                for (int i = 0; i < unitList.size(); i++) {
                    Uri photoUri = unitList.get(i).getPhotoUri();
                    if (photoUri != null) {
                        byte[] bytes = getFileData(photoUri);
                        if (bytes != null) {
                            d.put("unit_photo_" + i, new VolleyMultipartRequest.DataPart(
                                    "unit_" + i + ".jpg", bytes, "image/jpeg"));
                        }
                    }
                }
                return d;
            }
        };


        Volley.newRequestQueue(this).add(request);
    }
}