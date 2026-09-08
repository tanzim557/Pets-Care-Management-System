package com.petscare.controller;

import com.petscare.entity.Employee;
import com.petscare.entity.RescueTeam;
import com.petscare.repository.AdoptionPetRepository;
import com.petscare.repository.AdoptionRequestRepository;
import com.petscare.repository.EmployeeRepository;
import com.petscare.repository.RescueTeamRepository;
import com.petscare.utils.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private RescueTeamRepository rescueRepo;
    @Autowired
    private com.petscare.repository.EmergencyApplicationRepository emergencyRepo;
    @Autowired
    private com.petscare.repository.ProductRepository productRepo;
    @Autowired
    private com.petscare.repository.TreatmentRepository treatmentRepo;
    @Autowired
    private com.petscare.repository.AdoptionRequestRepository adoptionRequestRepo;
    @Autowired
    private com.petscare.repository.AdoptionPetRepository adoptionPetRepo;

    // Doctor Management
    @GetMapping("/doctor")
    public List<Employee> listDoctors() {
        // Assuming we can filter by role in repo or here
        return employeeRepo.findAll().stream()
                .filter(e -> "Doctor".equalsIgnoreCase(e.getRole()))
                .toList();
    }

    @PostMapping("/doctor")
    public ResponseEntity<?> addDoctor(@RequestBody Map<String, String> payload) {
        try {
            Employee doc = new Employee();

            String fullName = payload.get("name");
            if (fullName != null) {
                String[] parts = fullName.split(" ", 2);
                doc.setFirstName(parts[0]);
                doc.setLastName(parts.length > 1 ? parts[1] : "");
            } else {
                doc.setFirstName(payload.get("fname"));
                doc.setLastName(payload.get("lname"));
            }

            doc.setUsername(payload.get("username"));
            doc.setPassword(payload.get("password"));
            doc.setSpecialty(payload.get("specialty"));
            doc.setPhone(payload.get("phone"));
            doc.setLocation(payload.get("location"));
            doc.setPhoto(payload.get("photo"));
            doc.setAge(30); // Default
            doc.setGender("M"); // Default
            doc.setSalary(5000); // Default
            doc.setWorkingHours(8); // Default
            doc.setAdminId(1); // Default
            doc.setRole("Doctor");

            employeeRepo.save(doc);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage() != null ? e.getMessage() : "Unknown error occurred";
            return ResponseEntity.ok(Map.of("success", false, "message", msg));
        }
    }

    @PostMapping("/delete_doctor")
    public ResponseEntity<?> deleteDoctor(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("id"));

            // Unassign from treatments first
            List<com.petscare.entity.Treatment> treatments = treatmentRepo.findByDoctorId(id);

            for (com.petscare.entity.Treatment t : treatments) {
                t.setDoctorId(null);
                // Optionally set status back to 'New' or keep as is?
                // Keeping as is or maybe 'Doctor Unassigned' - but 'null' doctor usually
                // implies 'New' or 'RescueRequested' state depending on flow.
                // For safety and minimal side effect, we just nullify the doctor.
                treatmentRepo.save(t);
            }

            employeeRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Rescue Management
    @GetMapping("/rescue_list")
    public List<RescueTeam> listRescue() {
        return rescueRepo.findAll();
    }

    @PostMapping("/add_rescue")
    public ResponseEntity<?> addRescue(@RequestBody Map<String, String> payload) {
        try {
            RescueTeam rescue = new RescueTeam();

            if (payload.containsKey("name")) {
                String fullName = payload.get("name");
                String[] parts = fullName.split(" ", 2);
                rescue.setFirstName(parts[0]);
                rescue.setLastName(parts.length > 1 ? parts[1] : "");
            } else {
                rescue.setFirstName(payload.get("firstName"));
                rescue.setLastName(payload.get("lastName"));
            }

            rescue.setPhone(payload.get("phone"));
            rescue.setLocation(payload.get("location"));
            rescue.setPhoto(payload.get("photo"));
            rescue.setUsername(payload.get("username"));
            rescue.setPassword(payload.get("password"));
            rescue.setStatus("Available");

            rescueRepo.save(rescue);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage() != null ? e.getMessage() : "Unknown error occurred";
            return ResponseEntity.ok(Map.of("success", false, "message", msg));
        }
    }

    @PostMapping("/delete_rescue")
    public ResponseEntity<?> deleteRescue(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("id"));

            // Unassign from emergency applications first
            var apps = emergencyRepo.findAll().stream()
                    .filter(app -> id.equals(app.getAssignedRescueId()))
                    .toList();

            for (var app : apps) {
                app.setAssignedRescueId(null);
                emergencyRepo.save(app);
            }

            // Unassign from treatments
            var treatments = treatmentRepo.findAll().stream()
                    .filter(t -> id.equals(t.getRescueId()))
                    .toList();

            for (var t : treatments) {
                t.setRescueId(null);
                treatmentRepo.save(t);
            }

            rescueRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage() != null ? e.getMessage() : "Unknown error occurred";
            return ResponseEntity.ok(Map.of("success", false, "message", msg));
        }
    }

    // Shop Management
    @PostMapping("/add_product")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> payload) {
        try {
            com.petscare.entity.Product p = new com.petscare.entity.Product();
            p.setName((String) payload.get("name"));
            p.setCategory((String) payload.get("category"));
            p.setDescription((String) payload.get("description"));
            p.setPrice(Double.parseDouble(payload.get("price").toString()));
            p.setQuantity(Integer.parseInt(payload.get("quantity").toString()));
            p.setImageBase64((String) payload.get("image"));
            productRepo.save(p);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/delete_product")
    public ResponseEntity<?> deleteProduct(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("id"));
            productRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Treatment Management
    @GetMapping("/treatments/list")
    public List<Map<String, Object>> listTreatments() {
        return treatmentRepo.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId())) // Latest first
                .map(t -> {
                    var app = emergencyRepo.findById(t.getEmergencyAppId()).orElse(null);
                    String applicant = app != null ? app.getApplicantName() : "Unknown";
                    String animal = app != null ? app.getAnimalCategory() : "Unknown";

                    // Find doctor name if assigned
                    String doctorName = "Unassigned";
                    if (t.getDoctorId() != null) {
                        var doc = employeeRepo.findById(t.getDoctorId()).orElse(null);
                        if (doc != null)
                            doctorName = doc.getFirstName() + " " + doc.getLastName();
                    }

                    return Map.of(
                            "id", (Object) t.getId(),
                            "appId", t.getEmergencyAppId(),
                            "applicant", applicant,
                            "animal", animal,
                            "doctor", doctorName,
                            "status", t.getStatus() != null ? t.getStatus() : "Pending",
                            "updated", t.getLastUpdated() != null ? t.getLastUpdated().toString() : "N/A");
                }).toList();
    }

    // Adoption Management
    @GetMapping("/adoption/requests")
    public List<Map<String, Object>> listAdoptionRequests() {
        return adoptionRequestRepo.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId())) // Latest first
                .map(r -> {
                    var pet = adoptionPetRepo.findById(r.getPetId()).orElse(null);
                    String petName = pet != null ? pet.getName() : "Unknown Pet";
                    return Map.<String, Object>of(
                            "id", r.getId(),
                            "petName", petName,
                            "applicant", r.getApplicantName(),
                            "phone", r.getPhone(),
                            "reason", r.getReason(),
                            "status", r.getStatus());
                }).toList();
    }

    @PostMapping("/adoption/decide")
    public ResponseEntity<?> decideAdoption(@RequestBody Map<String, String> payload) {
        try {
            Integer id = Integer.parseInt(payload.get("requestId"));
            String status = payload.get("status");
            var req = adoptionRequestRepo.findById(id).orElseThrow();
            req.setStatus(status);
            adoptionRequestRepo.save(req);

            // If approved, mark pet as adopted
            if ("Approved".equalsIgnoreCase(status)) {
                var pet = adoptionPetRepo.findById(req.getPetId()).orElse(null);
                if (pet != null) {
                    pet.setStatus("Adopted");
                    adoptionPetRepo.save(pet);

                    // Send SMS notification to applicant
                    try {
                        String msg = "PetsCare: Congratulations! Your adoption request for " + pet.getName() +
                                " has been APPROVED. Please contact us to complete the adoption process. Thank you!";
                        SMSService.sendSMS(req.getPhone(), msg);
                        System.out.println("✓ Sent adoption approval SMS to: " + req.getPhone());
                    } catch (Exception smsEx) {
                        System.err.println("Failed to send adoption approval SMS: " + smsEx.getMessage());
                    }
                }
            } else if ("Rejected".equalsIgnoreCase(status)) {
                // Send SMS notification for rejection
                try {
                    var pet = adoptionPetRepo.findById(req.getPetId()).orElse(null);
                    String petName = (pet != null) ? pet.getName() : "the pet";
                    String msg = "PetsCare: We regret to inform you that your adoption request for " + petName +
                            " has been declined. Please contact us for more information.";
                    SMSService.sendSMS(req.getPhone(), msg);
                    System.out.println("✓ Sent adoption rejection SMS to: " + req.getPhone());
                } catch (Exception smsEx) {
                    System.err.println("Failed to send adoption rejection SMS: " + smsEx.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/adoption/move")
    public ResponseEntity<?> moveToAdoption(@RequestBody Map<String, String> payload) {
        try {
            Integer treatmentId = Integer.parseInt(payload.get("treatmentId"));
            var treatment = treatmentRepo.findById(treatmentId).orElseThrow();

            // Create Adoption Pet
            var app = emergencyRepo.findById(treatment.getEmergencyAppId()).orElseThrow();

            com.petscare.entity.AdoptionPet pet = new com.petscare.entity.AdoptionPet();
            pet.setName("Rescued " + app.getAnimalCategory()); // Or some name
            pet.setCategory(app.getAnimalCategory());
            // pet.setAge(0); // Field doesn't exist
            pet.setDescription("Recovered from: " + app.getDescription() + " | Rescued by: " + app.getApplicantName());
            pet.setImageBase64(app.getImageBase64());
            pet.setStatus("Available");
            // pet.setPrice(0.0); // Field doesn't exist

            adoptionPetRepo.save(pet);

            treatment.setStatus("Moved to Adoption");
            treatmentRepo.save(treatment);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Emergency Applications Management
    @GetMapping("/applications")
    public List<com.petscare.entity.EmergencyApplication> listApplications() {
        // Return latest applications first
        return emergencyRepo.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .toList();
    }
}
