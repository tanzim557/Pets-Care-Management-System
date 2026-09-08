package com.petscare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PetID")
    private Integer id;

    @Column(name = "Pet_Name", nullable = false)
    private String petName;

    @Column(name = "Age", nullable = false)
    private String age;

    @Column(name = "Species", nullable = false)
    private String species;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Column(name = "Pet_Type", nullable = false)
    private String petType;

    @Column(name = "ImagePath", nullable = false)
    private String imagePath;

    @Column(name = "AdminID", nullable = false)
    private Integer adminId;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}
