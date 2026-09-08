package com.petscare.repository;

import com.petscare.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByUsername(String username);

    List<Employee> findByRoleAndSpecialty(String role, String specialty);
}
