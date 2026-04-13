package com.example.boardinghousefinder.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.adapters.PendingReservationAdapter;
import com.example.boardinghousefinder.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerReservationsActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvEmpty;
    RecyclerView recyclerPending;

    private List<PendingReservationAdapter.PendingItem> items = new ArrayList<>();
    private PendingReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_reservations);

        btnBack        = findViewById(R.id.btnBackOwnerRes);
        tvEmpty        = findViewById(R.id.tvOwnerResEmpty);
        recyclerPending = findViewById(R.id.recyclerOwnerReservations);

        adapter = new PendingReservationAdapter(this, items);
        recyclerPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerPending.setAdapter(adapter);

        adapter.setOnActionListener(new PendingReservationAdapter.OnActionListener() {
            @Override
            public void onApprove(int reservationId, int position) {
                new AlertDialog.Builder(OwnerReservationsActivity.this)
                        .setTitle("Accept Reservation")
                        .setMessage("Accept this reservation request?")
                        .setPositiveButton("Accept", (d, w) -> updateReservation(reservationId, "approve", position))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onReject(int reservationId, int position) {
                new AlertDialog.Builder(OwnerReservationsActivity.this)
                        .setTitle("Reject Reservation")
                        .setMessage("Reject this reservation request?")
                        .setPositiveButton("Reject", (d, w) -> updateReservation(reservationId, "reject", position))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        fetchPendingReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPendingReservations();
    }

    private void fetchPendingReservations() {
        int ownerId = SessionManager.getUserId();
        String url = "http://192.168.254.104/casptone/fetch_pending_reservations.php?owner_id=" + ownerId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray arr = response.getJSONArray("reservations");
                            items.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                items.add(new PendingReservationAdapter.PendingItem(
                                        obj.getInt("id"),
                                        obj.optString("boarder_name", ""),
                                        obj.optString("boarder_email", ""),
                                        obj.optString("property_name", ""),
                                        obj.optString("unit_name", ""),
                                        obj.optString("move_in_date", ""),
                                        obj.optString("message", "")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error loading reservations", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void updateReservation(int reservationId, String action, int position) {
        String url = "http://192.168.254.104/casptone/update_reservation.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        items.remove(position);
                        adapter.notifyItemRemoved(position);
                        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                        String msg = action.equals("approve") ? "Reservation accepted!" : "Reservation rejected.";
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("reservation_id", String.valueOf(reservationId));
                p.put("action",         action);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}