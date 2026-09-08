package com.petscare.repository;

import com.petscare.entity.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RescueTeamRepository extends JpaRepository<RescueTeam, Integer> {
    Optional<RescueTeam> findByUsername(String username);

    List<RescueTeam> findByLocationContainingIgnoreCaseAndStatus(String location, String status);
}
