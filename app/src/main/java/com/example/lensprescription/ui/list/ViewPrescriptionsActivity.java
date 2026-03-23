package com.example.lensprescription.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lensprescription.databinding.ActivityViewPrescriptionsBinding;
import com.example.lensprescription.db.entity.Prescription;
import com.example.lensprescription.ui.addedit.AddPrescriptionActivity;
import com.example.lensprescription.ui.list.adapter.PrescriptionAdapter;
import com.example.lensprescription.ui.list.viewmodel.PrescriptionViewModel;

/**
 * Displays all saved prescriptions in a searchable RecyclerView.
 * Tap a card to edit; swipe left to delete; FAB to add new.
 */
public class ViewPrescriptionsActivity extends AppCompatActivity
        implements PrescriptionAdapter.OnItemActionListener {

    private ActivityViewPrescriptionsBinding binding;
    private PrescriptionViewModel viewModel;
    private PrescriptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPrescriptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Prescriptions");
        }

        viewModel = new ViewModelProvider(this).get(PrescriptionViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupSwipeToDelete();
        setupFab();
        observeData();
    }

    // ─── RecyclerView ─────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new PrescriptionAdapter();
        adapter.setOnItemActionListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    // ─── Search ───────────────────────────────────────────────────

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });
    }

    // ─── Swipe-to-delete ─────────────────────────────────────────

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@androidx.annotation.NonNull RecyclerView rv,
                                  @androidx.annotation.NonNull RecyclerView.ViewHolder vh,
                                  @androidx.annotation.NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder vh, int dir) {
                Prescription p = adapter.getCurrentList().get(vh.getAdapterPosition());
                confirmDelete(p);
            }
        }).attachToRecyclerView(binding.recyclerView);
    }

    // ─── FAB ──────────────────────────────────────────────────────

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddPrescriptionActivity.class)));
    }

    // ─── Observe ─────────────────────────────────────────────────

    private void observeData() {
        viewModel.getSearchResults().observe(this, prescriptions -> {
            adapter.submitList(prescriptions);
            boolean empty = prescriptions == null || prescriptions.isEmpty();
            binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    // ─── Listener callbacks ───────────────────────────────────────

    @Override
    public void onItemClick(Prescription prescription) {
        Intent intent = new Intent(this, AddPrescriptionActivity.class);
        intent.putExtra(AddPrescriptionActivity.EXTRA_PRESCRIPTION_ID, prescription.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Prescription prescription) {
        confirmDelete(prescription);
    }

    // ─── Delete dialog ────────────────────────────────────────────

    private void confirmDelete(Prescription prescription) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Prescription")
                .setMessage("Delete prescription for " + prescription.getPatientName() + "?")
                .setPositiveButton("Delete", (d, w) -> viewModel.delete(prescription))
                .setNegativeButton("Cancel", (d, w) -> adapter.notifyDataSetChanged())
                .setOnCancelListener(d -> adapter.notifyDataSetChanged())
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}