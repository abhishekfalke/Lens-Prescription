package com.example.lensprescription.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lensprescription.databinding.ActivityMainBinding;
import com.example.lensprescription.ui.common.BottomNavHelper;
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
                    count == 1 ? "1 record saved" : count + " Prescriptions saved");
        });

        // Tapping the record count card → go to Records tab
        binding.cardRecordCount.setOnClickListener(v -> {
            Intent i = new Intent(this, ViewPrescriptionsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });

        // Wire bottom nav — Home is active
        BottomNavHelper.setup(this, BottomNavHelper.NAV_HOME);
    }
}