package com.example.boardinghousefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardinghousefinder.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PendingReservationAdapter extends RecyclerView.Adapter<PendingReservationAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(int reservationId, int position);
        void onReject(int reservationId, int position);
    }

    private final Context context;
    private final List<PendingItem> items;
    private OnActionListener actionListener;

    public PendingReservationAdapter(Context context, List<PendingItem> items) {
        this.context = context;
        this.items   = items;
    }

    public void setOnActionListener(OnActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_reservation_owner, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PendingItem item = items.get(position);

        h.tvBoarderName.setText("👤 " + item.boarderName);
        h.tvBoarderEmail.setText("✉ " + item.boarderEmail);
        h.tvPropertyName.setText("🏠 " + item.propertyName);
        h.tvUnitName.setText(item.unitName != null && !item.unitName.isEmpty()
                ? "Unit: " + item.unitName : "Whole property");
        h.tvMoveInDate.setText("Move-in: " + item.moveInDate);
        h.tvMessage.setText(item.message != null && !item.message.isEmpty()
                ? "\"" + item.message + "\"" : "No message provided");

        h.btnApprove.setOnClickListener(v -> {
            if (actionListener != null)
                actionListener.onApprove(item.id, h.getAdapterPosition());
        });

        h.btnReject.setOnClickListener(v -> {
            if (actionListener != null)
                actionListener.onReject(item.id, h.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBoarderName, tvBoarderEmail, tvPropertyName,
                tvUnitName, tvMoveInDate, tvMessage;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBoarderName  = itemView.findViewById(R.id.tvPendingBoarderName);
            tvBoarderEmail = itemView.findViewById(R.id.tvPendingBoarderEmail);
            tvPropertyName = itemView.findViewById(R.id.tvPendingPropertyName);
            tvUnitName     = itemView.findViewById(R.id.tvPendingUnitName);
            tvMoveInDate   = itemView.findViewById(R.id.tvPendingMoveInDate);
            tvMessage      = itemView.findViewById(R.id.tvPendingMessage);
            btnApprove     = itemView.findViewById(R.id.btnApproveReservation);
            btnReject      = itemView.findViewById(R.id.btnRejectReservation);
        }
    }

    // ── Data model ───────────────────────────────────────────────────
    public static class PendingItem {
        public int    id;
        public String boarderName, boarderEmail;
        public String propertyName, unitName;
        public String moveInDate, message;

        public PendingItem(int id, String boarderName, String boarderEmail,
                           String propertyName, String unitName,
                           String moveInDate, String message) {
            this.id           = id;
            this.boarderName  = boarderName;
            this.boarderEmail = boarderEmail;
            this.propertyName = propertyName;
            this.unitName     = unitName;
            this.moveInDate   = moveInDate;
            this.message      = message;
        }
    }
}