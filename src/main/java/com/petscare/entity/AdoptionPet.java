package com.petscare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AdoptionPets")
public class AdoptionPet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdoptionPetID")
    private Integer id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Category", nullable = false)
    private String category;

    @Column(name = "Description")
    private String description;

    @Lob
    @Column(name = "ImageBase64", columnDefinition = "LONGTEXT")
    private String imageBase64;

    @Column(name = "OriginalTreatmentID")
    private Integer originalTreatmentId;

    @Column(name = "Status")
    private String status;

    @Column(name = "AddedDate")
    private LocalDateTime addedDate;

    @PrePersist
    protected void onCreate() {
        addedDate = LocalDateTime.now();
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Integer getOriginalTreatmentId() {
        return originalTreatmentId;
    }

    public void setOriginalTreatmentId(Integer originalTreatmentId) {
        this.originalTreatmentId = originalTreatmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
}
