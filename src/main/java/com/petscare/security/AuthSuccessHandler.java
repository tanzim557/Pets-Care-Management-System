package com.petscare.security;

import com.petscare.entity.Employee;
import com.petscare.entity.Admin;
import com.petscare.entity.Customer;
import com.petscare.entity.RescueTeam;
import com.petscare.repository.EmployeeRepository;
import com.petscare.repository.AdminRepository;
import com.petscare.repository.CustomerRepository;
import com.petscare.repository.RescueTeamRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RescueTeamRepository rescueTeamRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty() ? "" : authorities.iterator().next().getAuthority();
        String redirectUrl = "/index.html";

        String fullName = username;

        if (role.equals("ROLE_ADMIN")) {
            redirectUrl = "/admin.html";
            addCookie(response, "type", "admin");
            Optional<Admin> admin = adminRepository.findByUsername(username);
            if (admin.isPresent()) fullName = admin.get().getFirstName() + " " + admin.get().getLastName();
        } else if (role.equals("ROLE_DOCTOR")) {
            redirectUrl = "/doctor.html";
            addCookie(response, "type", "doctor");

            Optional<Employee> doc = employeeRepository.findByUsername(username);
            if (doc.isPresent()) {
                fullName = doc.get().getFirstName() + " " + doc.get().getLastName();
                addCookie(response, "role", doc.get().getRole());
                addCookie(response, "specialty", doc.get().getSpecialty());
                addCookie(response, "doctorId", String.valueOf(doc.get().getId()));

                if ("Rescue".equalsIgnoreCase(doc.get().getRole())) {
                    redirectUrl = "/rescue.html";
                }
            }
        } else if (role.equals("ROLE_RESCUE")) {
            redirectUrl = "/rescue.html";
            addCookie(response, "type", "rescue");
            Optional<RescueTeam> rescue = rescueTeamRepository.findByUsername(username);
            if (rescue.isPresent()) fullName = rescue.get().getFirstName() + " " + rescue.get().getLastName();
        } else {
            redirectUrl = "/client.html";
            addCookie(response, "type", "customer");
            Optional<Customer> customer = customerRepository.findByUsername(username);
            if (customer.isPresent()) fullName = customer.get().getFirstName() + " " + customer.get().getLastName();
        }

        try {
            addCookie(response, "fullName", java.net.URLEncoder.encode(fullName, "UTF-8"));
        } catch(Exception e) {}

        addCookie(response, "user", username);

        response.setContentType("application/json");
        response.getWriter().write("{\"success\":true, \"redirect\":\"" + redirectUrl + "\"}");
    }

    private void addCookie(HttpServletResponse response, String name, String value) {
        if (value == null) value = "";
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }
}
