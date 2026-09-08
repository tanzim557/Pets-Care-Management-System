package com.petscare.controller;

import com.petscare.entity.Donation;
import com.petscare.repository.DonationRepository;
import com.petscare.service.SSLCommerzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Date;

@RestController
@RequestMapping("/api/donation")
public class DonationController {

    @Autowired
    private DonationRepository donationRepo;

    @Autowired
    private SSLCommerzService sslCommerzService;

    @PostMapping("/init")
    public ResponseEntity<?> initDonation(@RequestBody Map<String, String> payload) {
        try {
            Donation d = new Donation();
            d.setDonorName(payload.get("name"));
            d.setPhone(payload.get("phone"));
            d.setAmount(Double.parseDouble(payload.get("amount")));
            d.setPurpose(payload.get("purpose"));
            d.setTransactionId(UUID.randomUUID().toString());
            d.setStatus("Pending");
            d.setDate(new Date());

            donationRepo.save(d);

            String gatewayUrl = sslCommerzService.initiatePayment(d);
            return ResponseEntity.ok(Map.of("success", true, "url", gatewayUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @RequestMapping(value = "/success", method = { RequestMethod.GET, RequestMethod.POST })
    public RedirectView paymentSuccess(@RequestParam Map<String, String> params) {
        String tranId = params.get("tran_id");
        Donation d = donationRepo.findByTransactionId(tranId).orElse(null);
        if (d != null) {
            d.setStatus("Successful");
            donationRepo.save(d);
            
            if (d.getPhone() != null && !d.getPhone().isEmpty()) {
                String msg = "Dear " + d.getDonorName() + ",\nThank you so much for your generous donation of " + d.getAmount() + " BDT to Pets Care! Your support helps us rescue and treat helpless animals. We are forever grateful for your kindness.\n- Pets Care Team";
                com.petscare.utils.SMSService.sendSMS(d.getPhone(), msg);
            }
        }
        return new RedirectView("/thank_you.html?type=donation");
    }

    @GetMapping("/list")
    public List<Donation> listDonations() {
        return donationRepo.findAll();
    }
}
