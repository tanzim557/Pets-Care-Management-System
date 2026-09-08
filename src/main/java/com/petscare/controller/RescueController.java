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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rescue")
public class RescueController {

    @Autowired
    private TreatmentRepository treatmentRepo;
    @Autowired
    private EmergencyApplicationRepository emergencyRepo;
    @Autowired
    private RescueTeamRepository rescueRepo;
    @Autowired
    private EmployeeRepository employeeRepo;

    @GetMapping("/{id}/missions")
    public List<Map<String, Object>> getMissions(@PathVariable("id") Integer rescueId) {
        List<Treatment> treatments = treatmentRepo.findByRescueId(rescueId);

        return treatments.stream()
                .<Map<String, Object>>map(t -> {
                    Optional<EmergencyApplication> appOpt = emergencyRepo.findById(t.getEmergencyAppId());
                    if (appOpt.isPresent()) {
                        EmergencyApplication app = appOpt.get();
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id", app.getId());
                        map.put("category", app.getAnimalCategory());
                        map.put("desc", app.getDescription() != null ? app.getDescription() : "");
                        map.put("location", app.getLocation() != null ? app.getLocation() : "");
                        map.put("area", app.getArea() != null ? app.getArea() : "");
                        map.put("phone", app.getPhone() != null ? app.getPhone() : "");
                        map.put("image", app.getImageBase64() != null ? app.getImageBase64() : "");
                        map.put("status", t.getStatus() != null ? t.getStatus() : "Assigned");
                        return map;
                    }
                    return null;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<?> completeRescue(@PathVariable("id") Integer emergencyId) {
        try {
            Optional<Treatment> tOpt = treatmentRepo.findTopByEmergencyAppIdOrderByIdDesc(emergencyId);
            if (tOpt.isPresent()) {
                Treatment t = tOpt.get();
                t.setStatus("Rescued");
                treatmentRepo.save(t);

                // Get Rescue Member Name
                String rescueName = "Rescue Team";
                if (t.getRescueId() != null) {
                    Optional<RescueTeam> rOpt = rescueRepo.findById(t.getRescueId());
                    if (rOpt.isPresent()) {
                        rescueName = rOpt.get().getFirstName() + " " + rOpt.get().getLastName();
                    }
                }

                EmergencyApplication app = emergencyRepo.findById(emergencyId).orElse(null);

                // Auto-Assign Doctor if not assigned
                if (t.getDoctorId() == null && app != null) {
                    String category = app.getAnimalCategory();
                    String area = app.getArea() != null ? app.getArea() : (app.getLocation() != null ? app.getLocation() : "");
                    
                    List<Employee> exactDoctors = employeeRepo.findByRoleAndSpecialty("Doctor", category);
                    List<Employee> allPetsDoctors = employeeRepo.findByRoleAndSpecialty("Doctor", "All Pets");
                    
                    List<Employee> allDoctors = new java.util.ArrayList<>();
                    allDoctors.addAll(exactDoctors);
                    allDoctors.addAll(allPetsDoctors);
                    
                    String lowerArea = area.toLowerCase();
                    List<Employee> localDoctors = allDoctors.stream()
                        .filter(d -> d.getLocation() != null && 
                                     (d.getLocation().toLowerCase().contains(lowerArea) || 
                                      lowerArea.contains(d.getLocation().toLowerCase())))
                        .toList();
                    
                    Employee assignedDoctor = null;
                    if (!localDoctors.isEmpty()) {
                        assignedDoctor = localDoctors.get(0);
                    } else if (!allDoctors.isEmpty()) {
                        assignedDoctor = allDoctors.get(0);
                    }
                    
                    if (assignedDoctor != null) {
                        t.setDoctorId(assignedDoctor.getId());
                        treatmentRepo.save(t);
                    }
                }

                // Notify Doctor
                if (t.getDoctorId() != null) {
                    Optional<Employee> empOpt = employeeRepo.findById(t.getDoctorId());
                    if (empOpt.isPresent()) {
                        String doctorPhone = empOpt.get().getPhone();
                        String msg = "Update: Rescue completed by " + rescueName + ". Animal is ready for checkup. App ID: #" + emergencyId;
                        SMSService.sendSMS(doctorPhone, msg);
                        return ResponseEntity
                                .ok(Map.of("success", true, "message", "Mission completed & Doctor assigned and notified"));
                    }
                }

                return ResponseEntity
                        .ok(Map.of("success", true, "message", "Mission completed (No doctor available to assign)"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Map.of("success", false, "message", "Error completing mission"));
    }
}
