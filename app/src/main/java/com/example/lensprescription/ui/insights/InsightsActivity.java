package com.example.lensprescription.ui.insights;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lensprescription.databinding.ActivityInsightsBinding;
import com.example.lensprescription.db.entity.Prescription;
import com.example.lensprescription.ui.list.viewmodel.PrescriptionViewModel;

import java.util.List;
import java.util.Locale;

/**
 * Shows a statistical summary and trend overview of all saved prescriptions.
 *
 * Visual trend charts would require a charting library (MPAndroidChart, etc.).
 * This implementation shows clear textual summaries that are immediately useful
 * without any extra dependencies.
 */
public class InsightsActivity extends AppCompatActivity {

    private ActivityInsightsBinding binding;
    private PrescriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Insights");
        }

        viewModel = new ViewModelProvider(this).get(PrescriptionViewModel.class);

        // Observe chronological list (oldest → newest) for trend analysis.
        viewModel.getAllChronological().observe(this, this::computeAndDisplay);
    }

    // ─── Analytics ────────────────────────────────────────────────

    private void computeAndDisplay(List<Prescription> list) {
        if (list == null || list.isEmpty()) {
            binding.groupInsights.setVisibility(View.GONE);
            binding.tvNoData.setVisibility(View.VISIBLE);
            return;
        }

        binding.groupInsights.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);

        int n = list.size();
        binding.tvTotalRecords.setText(String.valueOf(n));

        // Latest prescription
        Prescription latest = list.get(n - 1);
        binding.tvLatestPatient.setText(latest.getPatientName());
        binding.tvLatestRightSph.setText(Prescription.formatDiopter(latest.getRightSphere()));
        binding.tvLatestLeftSph.setText(Prescription.formatDiopter(latest.getLeftSphere()));

        if (n == 1) {
            binding.tvTrend.setText("Add more records to see trends.");
            return;
        }

        // ─── Sphere trend (first vs last) ─────────────────────────
        Prescription first = list.get(0);

        float rightChange = latest.getRightSphere() - first.getRightSphere();
        float leftChange  = latest.getLeftSphere()  - first.getLeftSphere();

        binding.tvTrend.setText(buildTrendText(rightChange, leftChange, n));

        // ─── Average sphere power ─────────────────────────────────
        float sumR = 0, sumL = 0;
        for (Prescription p : list) {
            sumR += p.getRightSphere();
            sumL += p.getLeftSphere();
        }
        binding.tvAvgRight.setText(Prescription.formatDiopter(sumR / n));
        binding.tvAvgLeft.setText(Prescription.formatDiopter(sumL / n));

        // ─── Most common axis ─────────────────────────────────────
        int[] axisCount = new int[181];
        for (Prescription p : list) {
            if (p.getRightAxis() >= 0 && p.getRightAxis() <= 180)
                axisCount[p.getRightAxis()]++;
            if (p.getLeftAxis()  >= 0 && p.getLeftAxis()  <= 180)
                axisCount[p.getLeftAxis()]++;
        }
        int peakAxis = 0, peakCount = 0;
        for (int i = 0; i <= 180; i++) {
            if (axisCount[i] > peakCount) { peakCount = axisCount[i]; peakAxis = i; }
        }
        binding.tvCommonAxis.setText(peakAxis + "°");
    }

    private String buildTrendText(float rightChange, float leftChange, int n) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(),
                "Over %d records — right eye sphere changed by %s D, left eye by %s D.",
                n,
                Prescription.formatDiopter(rightChange),
                Prescription.formatDiopter(leftChange)));

        if (rightChange < -0.5f || leftChange < -0.5f)
            sb.append("\n⚠ Vision appears to have become more myopic over time.");
        else if (rightChange > 0.5f || leftChange > 0.5f)
            sb.append("\n✓ Sphere has shifted in a positive direction.");
        else
            sb.append("\n✓ Prescription has remained relatively stable.");

        return sb.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}