package com.petscare.controller;

import com.petscare.entity.EmergencyApplication;
import com.petscare.entity.Employee;
import com.petscare.entity.RescueTeam;
import com.petscare.entity.Treatment;
import com.petscare.repository.EmergencyApplicationRepository;
import com.petscare.repository.EmployeeRepository;
import com.petscare.repository.RescueTeamRepository;
import com.petscare.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyController {

    @Autowired
    private EmergencyApplicationRepository emergencyRepo;
    @Autowired
    private RescueTeamRepository rescueRepo;
    @Autowired
    private TreatmentRepository treatmentRepo;
    @Autowired
    private EmployeeRepository employeeRepo;

    @PostMapping("/submit")
    public ResponseEntity<?> submitEmergency(@RequestBody Map<String, String> payload) {
        try {
            String area = payload.get("area");
            String category = payload.get("animalCategory");

            // 1. Find Rescue Team by Location
            List<RescueTeam> allRescue = rescueRepo.findAll();
            String lowerArea = area.toLowerCase();
            List<RescueTeam> localRescue = allRescue.stream()
                .filter(r -> r.getLocation() != null && 
                             (r.getLocation().toLowerCase().contains(lowerArea) || 
                              lowerArea.contains(r.getLocation().toLowerCase())))
                .toList();
            
            RescueTeam assignedRescue = null;
            if (!localRescue.isEmpty()) {
                assignedRescue = localRescue.get(0);
            } else if (!allRescue.isEmpty()) {
                assignedRescue = allRescue.get(0); // Fallback
            }

            // 2. Save Application
            EmergencyApplication app = new EmergencyApplication();
            app.setApplicantName(payload.get("applicantName"));
            app.setPhone(payload.get("phone"));
            app.setAnimalCategory(category);
            app.setDescription(payload.get("description"));
            app.setLocation(payload.get("location"));
            app.setArea(area);
            app.setImageBase64(payload.get("image"));
            if (assignedRescue != null) {
                app.setAssignedRescueId(assignedRescue.getId());
            }
            emergencyRepo.save(app);

            // 3. Create Treatment
            Treatment treatment = new Treatment();
            treatment.setEmergencyAppId(app.getId());
            treatment.setStatus("RescueRequested");
            if (assignedRescue != null) {
                treatment.setRescueId(assignedRescue.getId());
            }
            treatmentRepo.save(treatment);

            // 4. Send SMS to Rescue Team
            if (assignedRescue != null) {
                try {
                    String rescueMessage = String.format(
                            "🚨 PetsCare EMERGENCY ALERT: New %s rescue! " +
                                    "Applicant: %s, Phone: %s, Location: %s, Area: %s. " +
                                    "Please respond immediately. App ID: #%d",
                            category,
                            app.getApplicantName(),
                            app.getPhone(),
                            app.getLocation(),
                            area,
                            app.getId());
                    com.petscare.utils.SMSService.sendSMS(assignedRescue.getPhone(), rescueMessage);
                    System.out.println(
                            "✓ Emergency SMS sent to Rescue Team: " + assignedRescue.getFirstName());
                } catch (Exception smsEx) {
                    System.err.println("Failed to send emergency SMS to rescue team: " + smsEx.getMessage());
                }
            }

            String msg = "Emergency application submitted successfully!";
            if (assignedRescue != null) {
                msg += " A Rescue Team has been assigned and alerted.";
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", msg,
                    "id", app.getId(),
                    "category", category));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
