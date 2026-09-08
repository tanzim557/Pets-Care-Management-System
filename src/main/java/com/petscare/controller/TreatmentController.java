package com.petscare.controller;

import com.petscare.entity.Treatment;
import com.petscare.repository.TreatmentRepository;
import com.petscare.repository.EmergencyApplicationRepository;
import com.petscare.repository.AdoptionPetRepository;
import com.petscare.utils.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/treatment")
public class TreatmentController {

    @Autowired
    private TreatmentRepository treatmentRepo;

    @Autowired
    private EmergencyApplicationRepository emergencyRepo;

    @Autowired
    private AdoptionPetRepository adoptionPetRepo;

    @GetMapping("/get_treatment")
    public ResponseEntity<?> getTreatment(@RequestParam("appId") Integer appId) {
        Optional<Treatment> treatment = treatmentRepo.findTopByEmergencyAppIdOrderByIdDesc(appId);
        if (treatment.isPresent()) {
            Treatment t = treatment.get();
            return ResponseEntity.ok(Map.of(
                    "id", t.getId(),
                    "status", t.getStatus(),
                    "medicine", t.getMedicineAssigned() != null ? t.getMedicineAssigned() : "",
                    "notes", t.getNotes() != null ? t.getNotes() : ""));
        }
        return ResponseEntity.ok(Map.of());
    }

    @PostMapping("/update_status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("treatmentId"));
            Optional<Treatment> tOpt = treatmentRepo.findById(id);
            if (tOpt.isPresent()) {
                Treatment t = tOpt.get();
                String newStatus = payload.get("status");
                t.setStatus(newStatus);
                t.setNotes(payload.get("notes"));
                treatmentRepo.save(t);

                // Send SMS notification when marking as Completed/Healthy
                if ("Completed".equalsIgnoreCase(newStatus)) {
                    try {
                        var app = emergencyRepo.findById(t.getEmergencyAppId()).orElse(null);
                        if (app != null && app.getPhone() != null) {
                            String msg = "PetsCare: Great news! Your " + app.getAnimalCategory() +
                                    " has been treated and is now healthy. You can collect your pet. Thank you!";
                            SMSService.sendSMS(app.getPhone(), msg);
                            System.out.println("Sent healthy notification to: " + app.getPhone());
                        }
                    } catch (Exception smsEx) {
                        System.err.println("Failed to send SMS: " + smsEx.getMessage());
                    }
                }

                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    @PostMapping("/assign_medicine")
    public ResponseEntity<?> assignMedicine(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("treatmentId"));
            Optional<Treatment> tOpt = treatmentRepo.findById(id);
            if (tOpt.isPresent()) {
                Treatment t = tOpt.get();
                t.setMedicineAssigned(payload.get("medicine"));
                t.setDoctorId(Integer.parseInt(payload.get("doctorId")));
                t.setNotes(payload.get("notes"));
                t.setStatus("Under Treatment");
                treatmentRepo.save(t);
                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    @PostMapping("/move_to_adoption")
    public ResponseEntity<?> moveToAdoption(@RequestBody Map<String, String> payload) {
        try {
            Integer treatmentId = Integer.parseInt(payload.get("treatmentId"));
            var treatment = treatmentRepo.findById(treatmentId).orElseThrow();

            // Create Adoption Pet
            var app = emergencyRepo.findById(treatment.getEmergencyAppId()).orElseThrow();

            com.petscare.entity.AdoptionPet pet = new com.petscare.entity.AdoptionPet();
            pet.setName("Rescued " + app.getAnimalCategory()); 
            pet.setCategory(app.getAnimalCategory());
            pet.setDescription("Recovered from: " + app.getDescription() + " | Rescued by: " + app.getApplicantName());
            pet.setImageBase64(app.getImageBase64());
            pet.setStatus("Available");

            adoptionPetRepo.save(pet);

            treatment.setStatus("Moved to Adoption");
            treatmentRepo.save(treatment);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
