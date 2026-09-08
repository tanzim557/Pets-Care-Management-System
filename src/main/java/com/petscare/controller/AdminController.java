
package com.petscare.controller;

import com.petscare.entity.Order;
import com.petscare.repository.OrderRepository;
import com.petscare.repository.EmployeeRepository;
import com.petscare.repository.RescueTeamRepository;
import com.petscare.repository.TreatmentRepository;
import com.petscare.repository.DonationRepository;
import com.petscare.entity.Donation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderRepo.findAllByOrderByIdDesc();
    }

    @Autowired
    private com.petscare.repository.CustomerRepository customerRepo;
    @Autowired
    private com.petscare.repository.AdoptionPetRepository petRepo;
    @Autowired
    private com.petscare.repository.EmployeeRepository employeeRepo;
    @Autowired
    private com.petscare.repository.RescueTeamRepository rescueRepo;
    @Autowired
    private com.petscare.repository.TreatmentRepository treatmentRepo;
    @Autowired
    private com.petscare.repository.DonationRepository donationRepo;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        // Calculate total donations amount
        Double totalDonation = donationRepo.findAll().stream()
                .filter(d -> "Successful".equalsIgnoreCase(d.getStatus()))
                .mapToDouble(d -> d.getAmount())
                .sum();

        // Count recovered pets (Treatments marked as Completed)
        long recovered = treatmentRepo.findAll().stream()
                .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()))
                .count();

        // Count doctors
        long doctors = employeeRepo.findAll().stream()
                .filter(e -> "Doctor".equalsIgnoreCase(e.getRole()))
                .count();

        return Map.of(
                "users", customerRepo.count(), // Keep for legacy if needed, but UI will hide it
                "doctors", doctors,
                "rescuers", rescueRepo.count(),
                "recovered", recovered,
                "donations", totalDonation,
                "orders", orderRepo.count());
    }

    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("orderId"));
            String status = payload.get("status");
            String phone = payload.get("phone");

            Optional<Order> orderOpt = orderRepo.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(status);
                orderRepo.save(order);

                // Send SMS notification if phone number is provided
                if (phone != null && !phone.isEmpty()) {
                    try {
                        String message = getSMSMessage(status, order);
                        com.petscare.utils.SMSService.sendSMS(phone, message);
                    } catch (Exception smsEx) {
                        System.err.println("Failed to send order SMS: " + smsEx.getMessage());
                    }
                }

                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    private String getSMSMessage(String status, Order order) {
        String orderId = "#" + order.getId();
        switch (status) {
            case "Approved":
                return "PetsCare: Your order " + orderId
                        + " has been APPROVED! We're preparing your items for delivery. Total: ৳"
                        + order.getTotalAmount() + ". Thank you!";
            case "Rejected":
                return "PetsCare: We regret to inform you that your order " + orderId
                        + " has been declined. Please contact us for more information.";
            case "Delivered":
                return "PetsCare: Your order " + orderId
                        + " has been DELIVERED! Thank you for shopping with us. We hope you enjoy your purchase!";
            default:
                return "PetsCare: Your order " + orderId + " status has been updated to: " + status;
        }
    }

    @PostMapping("/orders/{id}/approve")
    public ResponseEntity<?> approveOrder(@PathVariable Integer id) {
        try {
            Optional<Order> orderOpt = orderRepo.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus("Approved");
                orderRepo.save(order);

                // Send SMS notification
                if (order.getPhone() != null && !order.getPhone().isEmpty()) {
                    try {
                        String message = "PetsCare: Your order #" + order.getId() +
                                " has been APPROVED! We are preparing it for delivery. Total: BDT " + order.getTotalAmount() +
                                ". Thank you!";
                        com.petscare.utils.SMSService.sendSMS(order.getPhone(), message);
                    } catch (Exception smsEx) {
                        System.err.println("Failed to send SMS: " + smsEx.getMessage());
                    }
                }

                return ResponseEntity.ok(Map.of("success", true, "message", "Order approved and SMS sent"));
            }
            return ResponseEntity.ok(Map.of("success", false, "message", "Order not found"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/orders/{id}/reject")
    public ResponseEntity<?> rejectOrder(@PathVariable Integer id) {
        try {
            Optional<Order> orderOpt = orderRepo.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus("Rejected");
                orderRepo.save(order);

                // Send SMS notification
                if (order.getPhone() != null && !order.getPhone().isEmpty()) {
                    try {
                        String message = "PetsCare: Your order #" + order.getId() +
                                " has been declined. Please contact us for more information.";
                        com.petscare.utils.SMSService.sendSMS(order.getPhone(), message);
                    } catch (Exception smsEx) {
                        System.err.println("Failed to send SMS: " + smsEx.getMessage());
                    }
                }

                return ResponseEntity.ok(Map.of("success", true, "message", "Order rejected and SMS sent"));
            }
            return ResponseEntity.ok(Map.of("success", false, "message", "Order not found"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
