package com.example.boardinghousefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class ReservationActivity extends AppCompatActivity {

    TextView tvPropertyName, tvPropertyType, tvPropertyPrice, tvResUnitName;
    EditText etMoveInDate, etMessage;
    Button btnSubmitReservation, btnCancelReservation;

    int boardingId, unitId;
    String propertyName, propertyType, propertyPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        tvPropertyName       = findViewById(R.id.tvResPropertyName);
        tvPropertyType       = findViewById(R.id.tvResPropertyType);
        tvPropertyPrice      = findViewById(R.id.tvResPropertyPrice);
        tvResUnitName        = findViewById(R.id.tvResUnitName);
        etMoveInDate         = findViewById(R.id.etMoveInDate);
        etMessage            = findViewById(R.id.etMessage);
        btnSubmitReservation = findViewById(R.id.btnSubmitReservation);
        btnCancelReservation = findViewById(R.id.btnCancelReservation);

        Intent i     = getIntent();
        boardingId    = i.getIntExtra("boarding_id", -1);
        unitId        = i.getIntExtra("unit_id", -1);
        propertyName  = i.getStringExtra("property_name");
        propertyType  = i.getStringExtra("property_type");
        propertyPrice = i.getStringExtra("property_price");
        String unitName = i.getStringExtra("unit_name");

        tvPropertyName.setText(propertyName);
        tvPropertyType.setText(propertyType);
        tvPropertyPrice.setText("₱" + propertyPrice + "/month");

        if (unitName != null && !unitName.isEmpty()) {
            tvResUnitName.setVisibility(View.VISIBLE);
            tvResUnitName.setText("Unit: " + unitName);
        } else {
            tvResUnitName.setVisibility(View.GONE);
        }

        btnCancelReservation.setOnClickListener(v -> finish());

        btnSubmitReservation.setOnClickListener(v -> {
            String moveInDate = etMoveInDate.getText().toString().trim();
            String message    = etMessage.getText().toString().trim();

            if (moveInDate.isEmpty()) {
                etMoveInDate.setError("Please enter your move-in date");
                etMoveInDate.requestFocus();
                return;
            }
            if (!moveInDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                etMoveInDate.setError("Use format: YYYY-MM-DD  e.g. 2025-08-01");
                etMoveInDate.requestFocus();
                return;
            }
            submitReservation(moveInDate, message);
        });
    }

    private void submitReservation(String moveInDate, String message) {
        String url = "http://192.168.254.104/casptone/reserve.php";
        btnSubmitReservation.setEnabled(false);
        btnSubmitReservation.setText("Submitting...");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(this,
                                "Reservation submitted! Check your Account tab for updates.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        btnSubmitReservation.setEnabled(true);
                        btnSubmitReservation.setText("Submit Reservation");
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnSubmitReservation.setEnabled(true);
                    btnSubmitReservation.setText("Submit Reservation");
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",      String.valueOf(SessionManager.getUserId()));
                params.put("boarding_id",  String.valueOf(boardingId));
                params.put("move_in_date", moveInDate);
                params.put("message",      message);
                if (unitId != -1) params.put("unit_id", String.valueOf(unitId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}