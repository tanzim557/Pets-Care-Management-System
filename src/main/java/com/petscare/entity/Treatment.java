package com.petscare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Treatments")
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TreatmentID")
    private Integer id;

    @Column(name = "EmergencyAppID", nullable = false)
    private Integer emergencyAppId;

    @Column(name = "RescueID")
    private Integer rescueId;

    @Column(name = "DoctorID")
    private Integer doctorId;

    @Column(name = "Status")
    private String status;

    @Column(name = "MedicineAssigned")
    private String medicineAssigned;

    @Column(name = "Notes")
    private String notes;

    @Column(name = "LastUpdated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmergencyAppId() {
        return emergencyAppId;
    }

    public void setEmergencyAppId(Integer emergencyAppId) {
        this.emergencyAppId = emergencyAppId;
    }

    public Integer getRescueId() {
        return rescueId;
    }

    public void setRescueId(Integer rescueId) {
        this.rescueId = rescueId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMedicineAssigned() {
        return medicineAssigned;
    }

    public void setMedicineAssigned(String medicineAssigned) {
        this.medicineAssigned = medicineAssigned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
