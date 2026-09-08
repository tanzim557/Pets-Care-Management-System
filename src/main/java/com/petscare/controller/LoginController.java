
package com.petscare.controller;

import com.petscare.entity.*;
import com.petscare.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private RescueTeamRepository rescueRepo;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest request,
            HttpServletResponse response) {
        String username = payload.get("username");
        String password = payload.get("password");
        String type = payload.get("type"); // customer, doctor, rescue, employee, admin

        try {
            // 1. Perform Authentication
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(token);

            // 2. Set Security Context
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            // 3. Determine Redirect & User ID based on Role/Type
            // Note: The UserDetailsService determines the role. We can verify if it matches
            // requested type.

            String redirect = "/index.html";
            Integer userId = 0;
            String specialty = null;

            // Fetch user details to get ID and specific redirect
            if ("admin".equalsIgnoreCase(type)) {
                Optional<Admin> admin = adminRepo.findByUsername(username);
                if (admin.isPresent()) {
                    redirect = "/admin.html";
                    userId = admin.get().getId();
                }
            } else if ("customer".equalsIgnoreCase(type)) {
                Optional<Customer> customer = customerRepo.findByUsername(username);
                if (customer.isPresent()) {
                    redirect = "/client.html";
                    userId = customer.get().getId();
                }
            } else if ("rescue".equalsIgnoreCase(type)) {
                Optional<RescueTeam> rescue = rescueRepo.findByUsername(username);
                if (rescue.isPresent()) {
                    redirect = "/rescue.html";
                    userId = rescue.get().getId();
                }
            } else {
                // Employee / Doctor
                Optional<Employee> employee = employeeRepo.findByUsername(username);
                if (employee.isPresent()) {
                    String role = employee.get().getRole();
                    userId = employee.get().getId();

                    if ("Doctor".equalsIgnoreCase(role)) {
                        redirect = "/doctor.html";
                        specialty = employee.get().getSpecialty(); // Get doctor's specialty
                    } else {
                        redirect = "/employee.html";
                    }
                }
            }

            // Set fullName cookie
            String fullName = username;
            if ("admin".equalsIgnoreCase(type)) {
                Optional<Admin> admin = adminRepo.findByUsername(username);
                if (admin.isPresent()) fullName = admin.get().getFirstName() + " " + admin.get().getLastName();
            } else if ("customer".equalsIgnoreCase(type)) {
                Optional<Customer> customer = customerRepo.findByUsername(username);
                if (customer.isPresent()) fullName = customer.get().getFirstName() + " " + customer.get().getLastName();
            } else if ("rescue".equalsIgnoreCase(type)) {
                Optional<RescueTeam> rescue = rescueRepo.findByUsername(username);
                if (rescue.isPresent()) fullName = rescue.get().getFirstName() + " " + rescue.get().getLastName();
            } else {
                Optional<Employee> employee = employeeRepo.findByUsername(username);
                if (employee.isPresent()) fullName = employee.get().getFirstName() + " " + employee.get().getLastName();
            }

            try {
                Cookie nameCookie = new Cookie("fullName", java.net.URLEncoder.encode(fullName, "UTF-8"));
                nameCookie.setPath("/");
                nameCookie.setMaxAge(24 * 60 * 60);
                response.addCookie(nameCookie);
            } catch(Exception e) {}

            // Build response map
            java.util.HashMap<String, Object> responseMap = new java.util.HashMap<>();
            responseMap.put("success", true);
            responseMap.put("redirect", redirect);
            responseMap.put("userId", userId);
            if (specialty != null) {
                responseMap.put("specialty", specialty);
            }

            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Invalid username or password"));
        }
    }
}
