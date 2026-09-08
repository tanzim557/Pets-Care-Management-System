package com.petscare.controller;

import com.petscare.entity.AdoptionPet;
import com.petscare.entity.AdoptionRequest;
import com.petscare.repository.AdoptionPetRepository;
import com.petscare.repository.AdoptionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionPetRepository adoptionPetRepository;

    @Autowired
    private AdoptionRequestRepository adoptionRequestRepository;

    @GetMapping("/list")
    public List<Map<String, Object>> getAdoptionList() {
        // Return pets WITH images included to match Shop behavior
        return adoptionPetRepository.findAllByOrderByIdDesc()
                .stream()
                .filter(pet -> "Available".equalsIgnoreCase(pet.getStatus()))
                .map(pet -> Map.of(
                        "id", (Object) pet.getId(),
                        "name", pet.getName(),
                        "category", pet.getCategory(),
                        "desc", pet.getDescription() != null ? pet.getDescription() : "",
                        "status", pet.getStatus(),
                        "image", pet.getImageBase64() != null ? pet.getImageBase64() : ""))
                .toList();
    }

    @GetMapping("/pet/{id}/image")
    public ResponseEntity<?> getPetImage(@PathVariable Integer id) {
        var pet = adoptionPetRepository.findById(id).orElse(null);
        if (pet != null && pet.getImageBase64() != null) {
            return ResponseEntity.ok(Map.of("image", pet.getImageBase64()));
        }
        return ResponseEntity.ok(Map.of("image", ""));
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestAdoption(@RequestBody Map<String, String> payload) {
        try {
            AdoptionRequest request = new AdoptionRequest();
            request.setPetId(Integer.parseInt(payload.get("petId")));
            request.setApplicantName(payload.get("name"));
            request.setPhone(payload.get("phone"));
            request.setEmail(payload.get("email"));
            request.setReason(payload.get("reason"));
            request.setStatus("Pending");

            adoptionRequestRepository.save(request);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
