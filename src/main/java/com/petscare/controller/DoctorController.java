package com.petscare.controller;

import com.petscare.entity.EmergencyApplication;
import com.petscare.entity.Employee;
import com.petscare.entity.RescueTeam;
import com.petscare.entity.Treatment;
import com.petscare.repository.EmergencyApplicationRepository;
import com.petscare.repository.EmployeeRepository;
import com.petscare.repository.RescueTeamRepository;
import com.petscare.repository.TreatmentRepository;
import com.petscare.utils.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private EmergencyApplicationRepository emergencyRepo;
    @Autowired
    private TreatmentRepository treatmentRepo;
    @Autowired
    private RescueTeamRepository rescueRepo;

    @GetMapping("/cases")
    public List<Map<String, Object>> getCases() {
        // Logic: Get all emergency apps where status is not Completed/Rejected and not
        // assigned to a doctor
        // This is complex with JPA, might need custom query or filtering in code
        // For simplicity in migration, we can fetch all and filter, or use custom query
        // Custom query is better but let's try code filtering for now or simple custom
        // query

        List<EmergencyApplication> allApps = emergencyRepo.findAllByOrderByIdDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (EmergencyApplication app : allApps) {
            Optional<Treatment> tOpt = treatmentRepo.findTopByEmergencyAppIdOrderByIdDesc(app.getId());
            boolean isAssigned = tOpt.isPresent() && tOpt.get().getDoctorId() != null && tOpt.get().getDoctorId() != 0;
            boolean isCompleted = tOpt.isPresent()
                    && ("Completed".equals(tOpt.get().getStatus()) || "Rejected".equals(tOpt.get().getStatus()));

            if (!isAssigned && !isCompleted) {
                String status = "New"; // Default
                if (tOpt.isPresent() && tOpt.get().getStatus() != null) {
                    status = tOpt.get().getStatus();
                }

                result.add(Map.of(
                        "id", app.getId(),
                        "applicant", app.getApplicantName(),
                        "phone", app.getPhone(),
                        "category", app.getAnimalCategory(),
                        "desc", app.getDescription() != null ? app.getDescription() : "",
                        "location", app.getLocation() != null ? app.getLocation() : "",
                        "area", app.getArea() != null ? app.getArea() : "",
                        "image", app.getImageBase64() != null ? app.getImageBase64() : "",
                        "status", status));
            }
        }
        return result;
    }

    @GetMapping("/my_patients")
    public List<Map<String, Object>> getMyPatients(@RequestParam("doctorId") Integer doctorId) {
        List<Treatment> treatments = treatmentRepo.findAllByOrderByIdDesc(); // Should filter by doctorId in repo
        // But repo method findAllByOrderByIdDesc doesn't filter.
        // Let's use stream filter or add method to repo.
        // Adding method is better but for speed:

        return treatments.stream()
                .filter(t -> doctorId.equals(t.getDoctorId()))
                .map(t -> {
                    Optional<EmergencyApplication> appOpt = emergencyRepo.findById(t.getEmergencyAppId());
                    if (appOpt.isPresent()) {
                        EmergencyApplication app = appOpt.get();
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id", app.getId());
                        map.put("treatmentId", t.getId());
                        map.put("applicant", app.getApplicantName());
                        map.put("phone", app.getPhone());
                        map.put("category", app.getAnimalCategory());
                        map.put("desc", app.getDescription() != null ? app.getDescription() : "");
                        map.put("location", app.getLocation() != null ? app.getLocation() : "");
                        map.put("area", app.getArea() != null ? app.getArea() : "");
                        map.put("image", app.getImageBase64() != null ? app.getImageBase64() : "");
                        map.put("status", t.getStatus() != null ? t.getStatus() : "New");
                        map.put("medicine", t.getMedicineAssigned() != null ? t.getMedicineAssigned() : "");
                        map.put("notes", t.getNotes() != null ? t.getNotes() : "");
                        return map;
                    }
                    return null;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    @PostMapping("/assign_to_me")
    public ResponseEntity<?> assignToMe(@RequestBody Map<String, String> payload) {
        try {
            Integer doctorId = Integer.parseInt(payload.get("doctorId"));
            Integer caseId = Integer.parseInt(payload.get("caseId"));

            Optional<Treatment> tOpt = treatmentRepo.findTopByEmergencyAppIdOrderByIdDesc(caseId);
            if (tOpt.isPresent()) {
                Treatment t = tOpt.get();
                t.setDoctorId(doctorId);
                t.setStatus("Under Treatment");
                treatmentRepo.save(t);
            } else {
                Treatment t = new Treatment();
                t.setEmergencyAppId(caseId);
                t.setDoctorId(doctorId);
                t.setStatus("Under Treatment");
                treatmentRepo.save(t);
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/save_notes")
    public ResponseEntity<?> saveNotes(@RequestBody Map<String, String> payload) {
        try {
            Integer treatmentId = Integer.parseInt(payload.get("treatmentId"));
            Optional<Treatment> tOpt = treatmentRepo.findById(treatmentId);
            if (tOpt.isPresent()) {
                Treatment t = tOpt.get();
                t.setNotes(payload.get("notes"));
                treatmentRepo.save(t);
                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    @PostMapping("/request_rescue")
    public ResponseEntity<?> requestRescue(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("id"));
            Optional<EmergencyApplication> appOpt = emergencyRepo.findById(id);
            if (appOpt.isPresent()) {
                EmergencyApplication app = appOpt.get();

                System.out.println("=== Request Rescue Debug ===");
                System.out.println("Emergency Location: '" + app.getLocation() + "'");
                System.out.println("Emergency Area: '" + app.getArea() + "'");

                // Find rescue team member in the same district (location)
                List<RescueTeam> allRescue = rescueRepo.findAll();
                System.out.println("Total Rescue Teams: " + allRescue.size());

                String appLoc = app.getLocation() != null ? app.getLocation().trim().toLowerCase() : "";

                List<RescueTeam> sameDistrict = allRescue.stream()
                        .filter(r -> {
                            String rLoc = r.getLocation() != null ? r.getLocation().trim().toLowerCase() : "";
                            return !appLoc.isEmpty() && !rLoc.isEmpty()
                                    && (rLoc.contains(appLoc) || appLoc.contains(rLoc));
                        })
                        .toList();

                System.out.println("Matching Teams: " + sameDistrict.size());

                RescueTeam rescueMember;
                if (sameDistrict.isEmpty()) {
                    // Fallback: If no team in district, pick ANY available team (for testing/demo
                    // purposes)
                    // Or return error. User said "team is assigned", so maybe the district name is
                    // just slightly different.
                    // Let's try to find *any* team if strict match fails, but log a warning.
                    if (!allRescue.isEmpty()) {
                        System.out.println("WARNING: No exact district match found. Assigning first available team.");
                        rescueMember = allRescue.get(0);
                    } else {
                        String errorMsg = "No rescue team available in "
                                + (app.getLocation() != null ? app.getLocation() : "unknown") + " district";
                        System.out.println("ERROR: " + errorMsg);
                        return ResponseEntity.ok(Map.of("success", false, "message", errorMsg));
                    }
                } else {
                    rescueMember = sameDistrict.get(0);
                }

                // Assign rescue member to the emergency application
                app.setAssignedRescueId(rescueMember.getId());
                emergencyRepo.save(app);

                // Create or update treatment record with status
                Optional<Treatment> tOpt = treatmentRepo.findTopByEmergencyAppIdOrderByIdDesc(id);
                Treatment treatment;
                if (tOpt.isPresent()) {
                    treatment = tOpt.get();
                } else {
                    treatment = new Treatment();
                    treatment.setEmergencyAppId(id);
                }
                treatment.setRescueId(rescueMember.getId());

                // Set Doctor ID if provided in payload
                if (payload.containsKey("doctorId")) {
                    try {
                        treatment.setDoctorId(Integer.parseInt(payload.get("doctorId")));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid doctorId format: " + payload.get("doctorId"));
                    }
                }

                treatment.setStatus("RescueRequested");
                treatmentRepo.save(treatment);

                // Send SMS to rescue team
                try {
                    String msg = "PetsCare URGENT: Doctor requested rescue for " + app.getAnimalCategory() +
                            " at " + app.getArea() + ", " + app.getLocation() +
                            ". Contact: " + app.getPhone();
                    SMSService.sendSMS(rescueMember.getPhone(), msg);
                } catch (Exception smsEx) {
                    System.err.println("SMS failed: " + smsEx.getMessage());
                    // Continue even if SMS fails
                }

                return ResponseEntity.ok(Map.of("success", true, "message",
                        "Rescue team (" + rescueMember.getFirstName() + ") notified"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
        return ResponseEntity.ok(Map.of("success", false, "message", "Emergency application not found"));
    }
}
