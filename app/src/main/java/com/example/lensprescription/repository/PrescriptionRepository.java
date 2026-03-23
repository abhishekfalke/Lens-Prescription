package com.example.lensprescription.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.lensprescription.db.AppDatabase;
import com.example.lensprescription.db.dao.PrescriptionDao;
import com.example.lensprescription.db.entity.Prescription;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Single source of truth between the ViewModel and the data layer.
 * Offloads all database writes to a fixed-thread pool so the main
 * thread is never blocked.
 */
public class PrescriptionRepository {

    private final PrescriptionDao dao;
    private final ExecutorService executor;

    // ─── Cached LiveData streams ──────────────────────────────────
    private final LiveData<List<Prescription>> allPrescriptions;
    private final LiveData<List<Prescription>> allChronological;

    public PrescriptionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.prescriptionDao();
        executor = Executors.newFixedThreadPool(2);

        allPrescriptions = dao.getAllPrescriptions();
        allChronological = dao.getAllChronological();
    }

    // ─── Reads (return LiveData — Room handles the background thread) ─

    public LiveData<List<Prescription>> getAllPrescriptions() {
        return allPrescriptions;
    }

    public LiveData<List<Prescription>> getAllChronological() {
        return allChronological;
    }

    public LiveData<Prescription> getPrescriptionById(int id) {
        return dao.getPrescriptionById(id);
    }

    public LiveData<List<Prescription>> getPrescriptionsByPatient(String name) {
        return dao.getPrescriptionsByPatient(name);
    }

    public LiveData<List<Prescription>> search(String query) {
        return dao.search(query);
    }

    // ─── Writes (must run off the main thread) ────────────────────

    public void insert(Prescription prescription) {
        executor.execute(() -> dao.insert(prescription));
    }

    public void update(Prescription prescription) {
        executor.execute(() -> dao.update(prescription));
    }

    public void delete(Prescription prescription) {
        executor.execute(() -> dao.delete(prescription));
    }

    public void deleteAll() {
        executor.execute(dao::deleteAll);
    }
}