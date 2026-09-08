package com.petscare.controller;

import com.petscare.entity.Medicine;
import com.petscare.entity.Order;
import com.petscare.entity.Pet;
import com.petscare.repository.*;
import com.petscare.utils.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MedicineRepository medicineRepo;
    @Autowired
    private EmergencyApplicationRepository emergencyRepo; // For track
    @Autowired
    private TreatmentRepository treatmentRepo; // For track status
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private PetRepository petRepo;
    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/medicines/list")
    public List<Medicine> getMedicines() {
        return medicineRepo.findAll();
    }

    @PostMapping("/track")
    public ResponseEntity<?> track(@RequestBody Map<String, String> payload) {
        try {
            String idStr = payload.get("id");
            String name = payload.get("name");
            String phone = payload.get("phone");

            Optional<com.petscare.entity.EmergencyApplication> appOpt = Optional.empty();

            // 1. Try to find by ID first if provided
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    Integer id = Integer.parseInt(idStr);
                    appOpt = emergencyRepo.findById(id);
                } catch (NumberFormatException e) {
                    // Invalid ID format, ignore
                }
            }

            // 2. If no ID match, try Name + Phone (Get LATEST by sorting)
            if (appOpt.isEmpty() && name != null && phone != null) {
                appOpt = emergencyRepo.findAll().stream()
                        .filter(app -> app.getApplicantName().equalsIgnoreCase(name) && app.getPhone().equals(phone))
                        .sorted((a, b) -> b.getId().compareTo(a.getId())) // Sort Descending by ID to get latest
                        .findFirst();
            }

            if (appOpt.isEmpty()) {
                return ResponseEntity
                        .ok(Map.of("success", false, "message", "No application found with these details."));
            }

            var app = appOpt.get();

            // Find the treatment record for this emergency application
            var treatment = treatmentRepo.findAll().stream()
                    .filter(t -> t.getEmergencyAppId().equals(app.getId()))
                    .findFirst();

            String status = "Emergency Submitted"; // Default
            String updated = app.getApplicationDate() != null ? app.getApplicationDate().toString() : "N/A";

            if (treatment.isPresent()) {
                var t = treatment.get();
                status = t.getStatus() != null ? t.getStatus() : "Under Treatment";
                updated = t.getLastUpdated() != null ? t.getLastUpdated().toString() : updated;
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "id", app.getId(),
                    "status", status,
                    "updated", updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", "Error tracking application"));
        }
    }

    @GetMapping("/employee/orders/list")
    public List<Order> getEmployeeOrders() {
        return orderRepo.findAllByOrderByIdDesc();
    }

    @PostMapping("/employee/orders/update_status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("orderId"));
            Optional<Order> orderOpt = orderRepo.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(payload.get("status"));
                // tracking number logic if needed
                orderRepo.save(order);

                // SMS logic here

                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    @GetMapping("/client/pets")
    public List<Pet> getClientPets() {
        // Should limit to 5
        return petRepo.findAll().stream().limit(5).toList();
    }

    @GetMapping("/client/orders")
    public List<Order> getClientOrders() {
        // Should limit to 5
        return orderRepo.findAllByOrderByIdDesc().stream().limit(5).toList();
    }
}
