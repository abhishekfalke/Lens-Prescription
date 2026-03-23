package com.example.lensprescription.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lensprescription.db.entity.Prescription;

import java.util.List;

/**
 * Data Access Object for the prescriptions table.
 * All long-running DB operations are observed via LiveData and
 * executed off the main thread by the repository.
 */
@Dao
public interface PrescriptionDao {

    // ─── Insert ──────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Prescription prescription);

    // ─── Update ──────────────────────────────────────────────────

    @Update
    void update(Prescription prescription);

    // ─── Delete ──────────────────────────────────────────────────

    @Delete
    void delete(Prescription prescription);

    @Query("DELETE FROM prescriptions")
    void deleteAll();

    // ─── Queries ─────────────────────────────────────────────────

    /** All prescriptions ordered newest first. */
    @Query("SELECT * FROM prescriptions ORDER BY dateMillis DESC")
    LiveData<List<Prescription>> getAllPrescriptions();

    /** Single prescription by primary key. */
    @Query("SELECT * FROM prescriptions WHERE id = :id LIMIT 1")
    LiveData<Prescription> getPrescriptionById(int id);

    /** Prescriptions for a specific patient, newest first. */
    @Query("SELECT * FROM prescriptions WHERE patientName = :name ORDER BY dateMillis DESC")
    LiveData<List<Prescription>> getPrescriptionsByPatient(String name);

    /** Total number of records. */
    @Query("SELECT COUNT(*) FROM prescriptions")
    int getCount();

    /** Prescriptions sorted oldest-first, useful for trend charts. */
    @Query("SELECT * FROM prescriptions ORDER BY dateMillis ASC")
    LiveData<List<Prescription>> getAllChronological();

    /** Search by patient name or doctor name (case-insensitive). */
    @Query("SELECT * FROM prescriptions " +
            "WHERE LOWER(patientName) LIKE '%' || LOWER(:query) || '%' " +
            "   OR LOWER(doctorName)  LIKE '%' || LOWER(:query) || '%' " +
            "ORDER BY dateMillis DESC")
    LiveData<List<Prescription>> search(String query);
}