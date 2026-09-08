package com.petscare.repository;

import com.petscare.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Integer> {
    Optional<Donation> findByTransactionId(String transactionId);
}
