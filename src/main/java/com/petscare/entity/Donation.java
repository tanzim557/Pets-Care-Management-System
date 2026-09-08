package com.petscare.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Donation")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DonationID")
    private Integer id;

    @Column(name = "DonorName")
    private String donorName;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "Amount")
    private Double amount;
    @Column(name = "Purpose")
    private String purpose;
    @Column(name = "TransactionID")
    private String transactionId;
    @Column(name = "Status")
    private String status; // Pending, Successful, Failed
    @Column(name = "Date")
    private Date date;

    public Donation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
