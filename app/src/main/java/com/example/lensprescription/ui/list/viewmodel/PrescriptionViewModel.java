package com.example.lensprescription.ui.list.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.lensprescription.db.entity.Prescription;
import com.example.lensprescription.repository.PrescriptionRepository;

import java.util.List;

/**
 * ViewModel shared across ViewPrescriptionsActivity, AddPrescriptionActivity,
 * and InsightsActivity.  Survives configuration changes.
 */
public class PrescriptionViewModel extends AndroidViewModel {

    private final PrescriptionRepository repository;

    // ─── All prescriptions (newest-first) ─────────────────────────
    private final LiveData<List<Prescription>> allPrescriptions;

    // ─── Chronological (for trend charts) ─────────────────────────
    private final LiveData<List<Prescription>> allChronological;

    // ─── Search ───────────────────────────────────────────────────
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Prescription>> searchResults;

    // ─── Currently selected prescription (for edit/detail) ────────
    private final MutableLiveData<Integer> selectedId = new MutableLiveData<>();
    private final LiveData<Prescription> selectedPrescription;

    public PrescriptionViewModel(@NonNull Application application) {
        super(application);
        repository = new PrescriptionRepository(application);

        allPrescriptions = repository.getAllPrescriptions();
        allChronological = repository.getAllChronological();

        // Whenever searchQuery changes, switch to new LiveData from DAO.
        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return repository.getAllPrescriptions();
            }
            return repository.search(query.trim());
        });

        // Whenever selectedId changes, load that prescription.
        selectedPrescription = Transformations.switchMap(selectedId,
                id -> repository.getPrescriptionById(id));
    }

    // ─── Expose LiveData ──────────────────────────────────────────

    public LiveData<List<Prescription>> getAllPrescriptions()  { return allPrescriptions; }
    public LiveData<List<Prescription>> getAllChronological()  { return allChronological; }
    public LiveData<List<Prescription>> getSearchResults()     { return searchResults; }
    public LiveData<Prescription>       getSelectedPrescription() { return selectedPrescription; }

    // ─── Commands ─────────────────────────────────────────────────

    public void insert(Prescription prescription)  { repository.insert(prescription); }
    public void update(Prescription prescription)  { repository.update(prescription); }
    public void delete(Prescription prescription)  { repository.delete(prescription); }
    public void deleteAll()                        { repository.deleteAll(); }

    public void setSearchQuery(String query)       { searchQuery.setValue(query); }
    public void selectPrescription(int id)         { selectedId.setValue(id); }
}