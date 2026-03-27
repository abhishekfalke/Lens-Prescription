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

@Dao
public interface PrescriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Prescription prescription);

    @Update
    void update(Prescription prescription);

    @Delete
    void delete(Prescription prescription);

    @Query("DELETE FROM prescriptions")
    void deleteAll();

    @Query("SELECT * FROM prescriptions ORDER BY dateMillis DESC")
    LiveData<List<Prescription>> getAllPrescriptions();

    @Query("SELECT * FROM prescriptions WHERE id = :id LIMIT 1")
    LiveData<Prescription> getPrescriptionById(int id);

    @Query("SELECT * FROM prescriptions WHERE patientName = :name ORDER BY dateMillis DESC")
    LiveData<List<Prescription>> getPrescriptionsByPatient(String name);

    @Query("SELECT COUNT(*) FROM prescriptions")
    int getCount();

    @Query("SELECT * FROM prescriptions ORDER BY dateMillis ASC")
    LiveData<List<Prescription>> getAllChronological();

    @Query("SELECT * FROM prescriptions " +
            "WHERE LOWER(patientName) LIKE '%' || LOWER(:query) || '%' " +
            "   OR LOWER(doctorName)  LIKE '%' || LOWER(:query) || '%' " +
            "ORDER BY dateMillis DESC")
    LiveData<List<Prescription>> search(String query);
}