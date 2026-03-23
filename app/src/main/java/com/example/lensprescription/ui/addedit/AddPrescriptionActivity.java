package com.example.lensprescription.ui.addedit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lensprescription.databinding.ActivityAddPrescriptionBinding;
import com.example.lensprescription.db.entity.Prescription;
import com.example.lensprescription.ui.list.viewmodel.PrescriptionViewModel;
import com.example.lensprescription.utils.ValidationUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Add a new prescription OR edit an existing one.
 *
 * Pass {@code EXTRA_PRESCRIPTION_ID} (int) to enter edit mode.
 * Default (no extra / id == -1) → add mode.
 */
public class AddPrescriptionActivity extends AppCompatActivity {

    public static final String EXTRA_PRESCRIPTION_ID = "prescription_id";

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private ActivityAddPrescriptionBinding binding;
    private PrescriptionViewModel viewModel;

    private long selectedDateMillis = System.currentTimeMillis();
    private int  editingId          = -1;   // -1 = add mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(PrescriptionViewModel.class);

        // ─── Edit mode? ───────────────────────────────────────────
        editingId = getIntent().getIntExtra(EXTRA_PRESCRIPTION_ID, -1);
        if (editingId != -1) {
            getSupportActionBar().setTitle("Edit Prescription");
            viewModel.selectPrescription(editingId);
            viewModel.getSelectedPrescription().observe(this, p -> {
                if (p != null) populateFields(p);
            });
        } else {
            getSupportActionBar().setTitle("Add Prescription");
            updateDateLabel();
        }

        // ─── Date picker ──────────────────────────────────────────
        binding.btnPickDate.setOnClickListener(v -> showDatePicker());

        // ─── Save ─────────────────────────────────────────────────
        binding.btnSave.setOnClickListener(v -> savePrescription());
    }

    // ─── DatePicker ───────────────────────────────────────────────

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDateMillis);

        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            selectedDateMillis = cal.getTimeInMillis();
            updateDateLabel();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateLabel() {
        binding.tvSelectedDate.setText(DATE_FMT.format(selectedDateMillis));
    }

    // ─── Populate for edit ────────────────────────────────────────

    private void populateFields(Prescription p) {
        selectedDateMillis = p.getDateMillis();
        updateDateLabel();

        binding.etPatientName.setText(p.getPatientName());
        binding.etDoctorName.setText(p.getDoctorName());
        binding.etClinic.setText(p.getClinic());

        // Right eye
        binding.etRightSphere.setText(String.format(Locale.getDefault(), "%+.2f", p.getRightSphere()));
        binding.etRightCylinder.setText(String.format(Locale.getDefault(), "%+.2f", p.getRightCylinder()));
        binding.etRightAxis.setText(String.valueOf(p.getRightAxis()));
        binding.etRightAdd.setText(String.format(Locale.getDefault(), "%.2f", p.getRightAdd()));

        // Left eye
        binding.etLeftSphere.setText(String.format(Locale.getDefault(), "%+.2f", p.getLeftSphere()));
        binding.etLeftCylinder.setText(String.format(Locale.getDefault(), "%+.2f", p.getLeftCylinder()));
        binding.etLeftAxis.setText(String.valueOf(p.getLeftAxis()));
        binding.etLeftAdd.setText(String.format(Locale.getDefault(), "%.2f", p.getLeftAdd()));

        // PD
        binding.etPdRight.setText(String.format(Locale.getDefault(), "%.1f", p.getPdRight()));
        binding.etPdLeft.setText(String.format(Locale.getDefault(), "%.1f", p.getPdLeft()));

        binding.etNotes.setText(p.getNotes());
    }

    // ─── Save / Validate ─────────────────────────────────────────

    private void savePrescription() {
        String patientName = getText(binding.etPatientName);
        String doctorName  = getText(binding.etDoctorName);
        String clinic      = getText(binding.etClinic);
        String notes       = getText(binding.etNotes);

        float rightSph = parseFloat(binding.etRightSphere, 0f);
        float rightCyl = parseFloat(binding.etRightCylinder, 0f);
        int   rightAx  = parseInt(binding.etRightAxis, 0);
        float rightAdd = parseFloat(binding.etRightAdd, 0f);

        float leftSph  = parseFloat(binding.etLeftSphere, 0f);
        float leftCyl  = parseFloat(binding.etLeftCylinder, 0f);
        int   leftAx   = parseInt(binding.etLeftAxis, 0);
        float leftAdd  = parseFloat(binding.etLeftAdd, 0f);

        float pdRight  = parseFloat(binding.etPdRight, 0f);
        float pdLeft   = parseFloat(binding.etPdLeft, 0f);

        // ─── Validate ─────────────────────────────────────────────
        ValidationUtils.ValidationResult result = ValidationUtils.validatePrescription(
                patientName,
                rightSph, rightCyl, rightAx, rightAdd,
                leftSph,  leftCyl,  leftAx,  leftAdd,
                pdRight,  pdLeft);

        if (!result.isValid) {
            Toast.makeText(this, result.errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        // ─── Build entity ─────────────────────────────────────────
        Prescription p = new Prescription(
                patientName, doctorName, clinic, selectedDateMillis,
                rightSph, rightCyl, rightAx, rightAdd,
                leftSph,  leftCyl,  leftAx,  leftAdd,
                pdRight, pdLeft, notes);

        if (editingId != -1) {
            p.setId(editingId);
            viewModel.update(p);
            Toast.makeText(this, "Prescription updated!", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(p);
            Toast.makeText(this, "Prescription saved!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    // ─── Input helpers ────────────────────────────────────────────

    private String getText(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private float parseFloat(com.google.android.material.textfield.TextInputEditText et, float def) {
        return ValidationUtils.parseDiopter(getText(et), def);
    }

    private int parseInt(com.google.android.material.textfield.TextInputEditText et, int def) {
        return ValidationUtils.parseAxis(getText(et), def);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}