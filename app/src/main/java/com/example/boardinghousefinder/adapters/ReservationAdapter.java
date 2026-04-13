package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardinghousefinder.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    public interface OnCancelClickListener {
        void onCancelClick(int reservationId, int position);
    }

    private final Context context;
    private final List<ReservationItem> items;
    private OnCancelClickListener cancelListener;

    public ReservationAdapter(Context context, List<ReservationItem> items) {
        this.context = context;
        this.items   = items;
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_reservation_boarder, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ReservationItem item = items.get(position);

        h.tvPropertyName.setText(item.propertyName);
        h.tvUnitName.setText(item.unitName != null && !item.unitName.isEmpty()
                ? "Unit: " + item.unitName : "Whole property");
        h.tvMoveInDate.setText("Move-in: " + item.moveInDate);
        h.tvStatus.setText(item.status.toUpperCase());

        // Status badge color
        switch (item.status.toLowerCase()) {
            case "approved":
                h.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
                h.tvStatus.setTextColor(Color.WHITE);
                // Show owner contact
                h.layoutOwnerContact.setVisibility(View.VISIBLE);
                h.tvOwnerName.setText("👤 " + item.ownerName);
                h.tvOwnerEmail.setText("✉ " + item.ownerEmail);
                h.tvOwnerPhone.setText("📞 " + item.ownerPhone);
                h.btnCancel.setVisibility(View.GONE);
                break;
            case "rejected":
                h.tvStatus.setBackgroundColor(Color.parseColor("#F44336"));
                h.tvStatus.setTextColor(Color.WHITE);
                h.layoutOwnerContact.setVisibility(View.GONE);
                h.btnCancel.setVisibility(View.GONE);
                break;
            default: // pending
                h.tvStatus.setBackgroundColor(Color.parseColor("#FF9800"));
                h.tvStatus.setTextColor(Color.WHITE);
                h.layoutOwnerContact.setVisibility(View.GONE);
                h.btnCancel.setVisibility(View.VISIBLE);
                break;
        }

        h.btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancelClick(item.id, h.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPropertyName, tvUnitName, tvMoveInDate, tvStatus;
        TextView tvOwnerName, tvOwnerEmail, tvOwnerPhone;
        LinearLayout layoutOwnerContact;
        MaterialButton btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropertyName     = itemView.findViewById(R.id.tvResPropertyName);
            tvUnitName         = itemView.findViewById(R.id.tvResUnitName);
            tvMoveInDate       = itemView.findViewById(R.id.tvResMoveInDate);
            tvStatus           = itemView.findViewById(R.id.tvResStatus);
            tvOwnerName        = itemView.findViewById(R.id.tvResOwnerName);
            tvOwnerEmail       = itemView.findViewById(R.id.tvResOwnerEmail);
            tvOwnerPhone       = itemView.findViewById(R.id.tvResOwnerPhone);
            layoutOwnerContact = itemView.findViewById(R.id.layoutOwnerContact);
            btnCancel          = itemView.findViewById(R.id.btnCancelReservation);
        }
    }

    // ── Data model ───────────────────────────────────────────────────
    public static class ReservationItem {
        public int    id;
        public String propertyName, unitName, moveInDate, status;
        public String ownerName, ownerEmail, ownerPhone;

        public ReservationItem(int id, String propertyName, String unitName,
                               String moveInDate, String status,
                               String ownerName, String ownerEmail, String ownerPhone) {
            this.id           = id;
            this.propertyName = propertyName;
            this.unitName     = unitName;
            this.moveInDate   = moveInDate;
            this.status       = status;
            this.ownerName    = ownerName;
            this.ownerEmail   = ownerEmail;
            this.ownerPhone   = ownerPhone;
        }
    }
}