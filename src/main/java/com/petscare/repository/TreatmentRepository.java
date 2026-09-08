package com.petscare.repository;

import com.petscare.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TreatmentRepository extends JpaRepository<Treatment, Integer> {
    List<Treatment> findAllByOrderByIdDesc();

    Optional<Treatment> findTopByEmergencyAppIdOrderByIdDesc(Integer emergencyAppId);

    List<Treatment> findByRescueId(Integer rescueId);

    List<Treatment> findByDoctorId(Integer doctorId);
}
