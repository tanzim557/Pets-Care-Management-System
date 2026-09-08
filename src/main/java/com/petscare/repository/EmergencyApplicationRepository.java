package com.petscare.repository;

import com.petscare.entity.EmergencyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmergencyApplicationRepository extends JpaRepository<EmergencyApplication, Integer> {
    List<EmergencyApplication> findAllByOrderByIdDesc();
}
