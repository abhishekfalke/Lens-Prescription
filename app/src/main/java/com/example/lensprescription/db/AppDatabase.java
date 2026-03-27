package com.example.lensprescription.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.lensprescription.db.dao.PrescriptionDao;
import com.example.lensprescription.db.entity.Prescription;

/**
 * Singleton Room database.
 * Increment the version number and supply a migration strategy whenever
 * the schema changes (entities added/removed, columns changed).
 */
@Database(entities = {Prescription.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "lens_prescription_db";

    // Volatile guarantees visibility across threads without synchronization overhead.
    private static volatile AppDatabase instance;

    public abstract PrescriptionDao prescriptionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}