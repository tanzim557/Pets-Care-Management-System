package com.petscare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PaymentConfig")
public class PaymentConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConfigID")
    private Integer id;

    @Column(name = "StoreId")
    private String storeId;

    @Column(name = "StorePassword")
    private String storePassword;

    @Column(name = "IsLive")
    private boolean isLive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStorePassword() {
        return storePassword;
    }

    public void setStorePassword(String storePassword) {
        this.storePassword = storePassword;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
