package com.petscare.service;

import com.petscare.entity.Admin;
import com.petscare.entity.Customer;
import com.petscare.entity.Employee;
import com.petscare.entity.RescueTeam;
import com.petscare.repository.AdminRepository;
import com.petscare.repository.CustomerRepository;
import com.petscare.repository.EmployeeRepository;
import com.petscare.repository.RescueTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try Admin
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return User.builder()
                    .username(admin.get().getUsername())
                    .password("{noop}" + admin.get().getPassword()) // NoOp for plain text passwords
                    .roles("ADMIN")
                    .build();
        }

        // Try Employee (Doctor)
        Optional<Employee> employee = employeeRepository.findByUsername(username);
        if (employee.isPresent()) {
            return User.builder()
                    .username(employee.get().getUsername())
                    .password("{noop}" + employee.get().getPassword())
                    .roles("DOCTOR") // Assuming all employees are doctors for now, or check role
                    .build();
        }

        // Try RescueTeam
        Optional<RescueTeam> rescue = rescueTeamRepository.findByUsername(username);
        if (rescue.isPresent()) {
            return User.builder()
                    .username(rescue.get().getUsername())
                    .password("{noop}" + rescue.get().getPassword())
                    .roles("RESCUE")
                    .build();
        }

        // Try Customer
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            return User.builder()
                    .username(customer.get().getUsername())
                    .password("{noop}" + customer.get().getPassword())
                    .roles("CUSTOMER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
