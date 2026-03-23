package com.example.lensprescription.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lensprescription.databinding.ActivityMainBinding;
import com.example.lensprescription.ui.addedit.AddPrescriptionActivity;
import com.example.lensprescription.ui.insights.InsightsActivity;
import com.example.lensprescription.ui.list.ViewPrescriptionsActivity;
import com.example.lensprescription.ui.list.viewmodel.PrescriptionViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PrescriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PrescriptionViewModel.class);

        viewModel.getAllPrescriptions().observe(this, list -> {
            int count = (list != null) ? list.size() : 0;
            binding.tvRecordCount.setText(
                    count == 1 ? "1 record saved" : "✅ " + count + " records saved");
        });

        binding.btnAddPrescription.setOnClickListener(v ->
                startActivity(new Intent(this, AddPrescriptionActivity.class)));

        binding.btnViewPrescriptions.setOnClickListener(v ->
                startActivity(new Intent(this, ViewPrescriptionsActivity.class)));

        binding.btnInsights.setOnClickListener(v ->
                startActivity(new Intent(this, InsightsActivity.class)));
    }
}