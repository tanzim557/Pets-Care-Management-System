package com.petscare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Medicines")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MedicineID")
    private Integer id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Type", nullable = false)
    private String type;

    @Column(name = "DosageInfo")
    private String dosageInfo;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDosageInfo() {
        return dosageInfo;
    }

    public void setDosageInfo(String dosageInfo) {
        this.dosageInfo = dosageInfo;
    }
}
