package com.petscare.repository;

import com.petscare.entity.AdoptionPet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdoptionPetRepository extends JpaRepository<AdoptionPet, Integer> {
    List<AdoptionPet> findAllByOrderByIdDesc();
}
