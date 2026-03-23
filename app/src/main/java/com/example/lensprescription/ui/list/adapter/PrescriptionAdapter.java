package com.example.lensprescription.ui.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lensprescription.R;
import com.example.lensprescription.db.entity.Prescription;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RecyclerView adapter using ListAdapter + DiffUtil for efficient diffing.
 * Exposes click and long-click (delete) callbacks via the listener interface.
 */
public class PrescriptionAdapter extends ListAdapter<Prescription, PrescriptionAdapter.PrescriptionViewHolder> {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    // ─── Listener interface ───────────────────────────────────────

    public interface OnItemActionListener {
        void onItemClick(Prescription prescription);
        void onDeleteClick(Prescription prescription);
    }

    private OnItemActionListener listener;

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    // ─── DiffUtil callback ────────────────────────────────────────

    private static final DiffUtil.ItemCallback<Prescription> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Prescription>() {
                @Override
                public boolean areItemsTheSame(@NonNull Prescription a, @NonNull Prescription b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Prescription a, @NonNull Prescription b) {
                    // Compare all meaningful fields
                    return a.getDateMillis()    == b.getDateMillis()
                            && a.getPatientName().equals(b.getPatientName())
                            && a.getRightSphere()   == b.getRightSphere()
                            && a.getLeftSphere()    == b.getLeftSphere();
                }
            };

    public PrescriptionAdapter() {
        super(DIFF_CALLBACK);
    }

    // ─── ViewHolder ───────────────────────────────────────────────

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPatientName;
        final TextView tvDate;
        final TextView tvDoctor;
        final TextView tvRightEye;
        final TextView tvLeftEye;
        final ImageButton btnDelete;

        PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate        = itemView.findViewById(R.id.tvDate);
            tvDoctor      = itemView.findViewById(R.id.tvDoctor);
            tvRightEye    = itemView.findViewById(R.id.tvRightEye);
            tvLeftEye     = itemView.findViewById(R.id.tvLeftEye);
            btnDelete     = itemView.findViewById(R.id.btnDelete);
        }
    }

    // ─── Adapter overrides ────────────────────────────────────────

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription p = getItem(position);

        holder.tvPatientName.setText(p.getPatientName());
        holder.tvDate.setText(DATE_FMT.format(new Date(p.getDateMillis())));
        holder.tvDoctor.setText(p.getDoctorName() != null && !p.getDoctorName().isEmpty()
                ? "Dr. " + p.getDoctorName() : "");

        // Format: SPH / CYL × Axis
        holder.tvRightEye.setText(formatEye(
                p.getRightSphere(), p.getRightCylinder(), p.getRightAxis()));
        holder.tvLeftEye.setText(formatEye(
                p.getLeftSphere(), p.getLeftCylinder(), p.getLeftAxis()));

        // Click → open detail / edit
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(p);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(p);
        });
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private String formatEye(float sphere, float cylinder, int axis) {
        return String.format(Locale.getDefault(),
                "%s / %s × %d°",
                Prescription.formatDiopter(sphere),
                Prescription.formatDiopter(cylinder),
                axis);
    }
}