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
import com.example.boardinghousefinder.adapters.ReservationAdapter;
import com.example.boardinghousefinder.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoarderReservationsActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvEmpty;
    RecyclerView recyclerReservations;

    private List<ReservationAdapter.ReservationItem> items = new ArrayList<>();
    private ReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarder_reservations);

        btnBack              = findViewById(R.id.btnBackBoarderRes);
        tvEmpty              = findViewById(R.id.tvBoarderResEmpty);
        recyclerReservations = findViewById(R.id.recyclerBoarderReservations);

        adapter = new ReservationAdapter(this, items);
        recyclerReservations.setLayoutManager(new LinearLayoutManager(this));
        recyclerReservations.setAdapter(adapter);

        adapter.setOnCancelClickListener((reservationId, position) ->
                new AlertDialog.Builder(this)
                        .setTitle("Cancel Reservation")
                        .setMessage("Are you sure you want to cancel this reservation?")
                        .setPositiveButton("Yes, Cancel", (d, w) -> cancelReservation(reservationId, position))
                        .setNegativeButton("No", null)
                        .show()
        );

        btnBack.setOnClickListener(v -> finish());

        fetchReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchReservations();
    }

    private void fetchReservations() {
        int userId = SessionManager.getUserId();
        String url = "http://192.168.254.104/casptone/fetch_my_reservations.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray arr = response.getJSONArray("reservations");
                            items.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                items.add(new ReservationAdapter.ReservationItem(
                                        obj.getInt("id"),
                                        obj.optString("property_name", ""),
                                        obj.optString("unit_name", ""),
                                        obj.optString("move_in_date", ""),
                                        obj.optString("status", "pending"),
                                        obj.optString("owner_name", ""),
                                        obj.optString("owner_email", ""),
                                        obj.optString("owner_phone", "")
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

    private void cancelReservation(int reservationId, int position) {
        String url = "http://192.168.254.104/casptone/cancel_reservation.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        items.remove(position);
                        adapter.notifyItemRemoved(position);
                        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Could not cancel: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("reservation_id", String.valueOf(reservationId));
                p.put("user_id",        String.valueOf(SessionManager.getUserId()));
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}