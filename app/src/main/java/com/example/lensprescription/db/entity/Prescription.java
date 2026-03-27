package com.example.lensprescription.db.entity;

import android.annotation.SuppressLint;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity representing a single eye prescription record.
 * Eye prescription values:
 *  - Sphere (SPH): lens power, negative = myopia, positive = hyperopia (-20.00 to +20.00)
 *  - Cylinder (CYL): astigmatism correction, usually negative (-10.00 to +10.00)
 *  - Axis: orientation of CYL in degrees (0–180)
 *  - Add: reading addition for presbyopia (0.00 to +4.00)
 *  - PD: Pupillary distance in mm
 */

@Entity(tableName = "prescriptions")
public class Prescription {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // ─── Meta ───────────────────────────────────────────────
    private String patientName;
    private String doctorName;
    private String clinic;
    private long dateMillis;
    private String notes;

    private float rightSphere;
    private float rightCylinder;
    private int   rightAxis;
    private float rightAdd;

    private float leftSphere;
    private float leftCylinder;
    private int   leftAxis;
    private float leftAdd;

    private float pdRight;
    private float pdLeft;

    public Prescription() {}

    public Prescription(String patientName, String doctorName, String clinic, long dateMillis,
                        float rightSphere, float rightCylinder, int rightAxis, float rightAdd,
                        float leftSphere,  float leftCylinder,  int leftAxis,  float leftAdd,
                        float pdRight, float pdLeft, String notes) {
        this.patientName   = patientName;
        this.doctorName    = doctorName;
        this.clinic        = clinic;
        this.dateMillis    = dateMillis;
        this.rightSphere   = rightSphere;
        this.rightCylinder = rightCylinder;
        this.rightAxis     = rightAxis;
        this.rightAdd      = rightAdd;
        this.leftSphere    = leftSphere;
        this.leftCylinder  = leftCylinder;
        this.leftAxis      = leftAxis;
        this.leftAdd       = leftAdd;
        this.pdRight       = pdRight;
        this.pdLeft        = pdLeft;
        this.notes         = notes;
    }


    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getPatientName()                        { return patientName; }
    public void   setPatientName(String patientName)      { this.patientName = patientName; }

    public String getDoctorName()                         { return doctorName; }
    public void   setDoctorName(String doctorName)        { this.doctorName = doctorName; }

    public String getClinic()                             { return clinic; }
    public void   setClinic(String clinic)                { this.clinic = clinic; }

    public long   getDateMillis()                         { return dateMillis; }
    public void   setDateMillis(long dateMillis)          { this.dateMillis = dateMillis; }

    public String getNotes()                              { return notes; }
    public void   setNotes(String notes)                  { this.notes = notes; }

    // Right eye
    public float getRightSphere()                         { return rightSphere; }
    public void  setRightSphere(float rightSphere)        { this.rightSphere = rightSphere; }

    public float getRightCylinder()                       { return rightCylinder; }
    public void  setRightCylinder(float rightCylinder)    { this.rightCylinder = rightCylinder; }

    public int   getRightAxis()                           { return rightAxis; }
    public void  setRightAxis(int rightAxis)              { this.rightAxis = rightAxis; }

    public float getRightAdd()                            { return rightAdd; }
    public void  setRightAdd(float rightAdd)              { this.rightAdd = rightAdd; }

    // Left eye
    public float getLeftSphere()                          { return leftSphere; }
    public void  setLeftSphere(float leftSphere)          { this.leftSphere = leftSphere; }

    public float getLeftCylinder()                        { return leftCylinder; }
    public void  setLeftCylinder(float leftCylinder)      { this.leftCylinder = leftCylinder; }

    public int   getLeftAxis()                            { return leftAxis; }
    public void  setLeftAxis(int leftAxis)                { this.leftAxis = leftAxis; }

    public float getLeftAdd()                             { return leftAdd; }
    public void  setLeftAdd(float leftAdd)                { this.leftAdd = leftAdd; }

    // PD
    public float getPdRight()                             { return pdRight; }
    public void  setPdRight(float pdRight)                { this.pdRight = pdRight; }

    public float getPdLeft()                              { return pdLeft; }
    public void  setPdLeft(float pdLeft)                  { this.pdLeft = pdLeft; }

    public float getBinocularPd() { return pdRight + pdLeft; }

    @SuppressLint("DefaultLocale")
    public static String formatDiopter(float value) {
        return String.format("%+.2f", value);
    }
}