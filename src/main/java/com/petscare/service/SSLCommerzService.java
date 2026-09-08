package com.petscare.service;

import com.petscare.entity.Order;
import com.petscare.entity.PaymentConfig;
import com.petscare.repository.PaymentConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class SSLCommerzService {

    @Autowired
    private PaymentConfigRepository paymentConfigRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public String initiatePayment(Order order) {
        PaymentConfig config = paymentConfigRepository.findAll().stream().findFirst().orElse(null);
        if (config == null)
            throw new RuntimeException("Payment configuration not found");

        if ("store1".equals(config.getStoreId())) {
            return "http://localhost:8081/api/payment/success?tran_id=" + order.getTransactionId();
        }

        return sendPaymentRequest(config, order.getTotalAmount(), order.getTransactionId(), order.getCustomerName(),
                order.getPhone(), "Pet Supplies", "http://localhost:8081/api/payment/success");
    }

    public String initiatePayment(com.petscare.entity.Donation donation) {
        PaymentConfig config = paymentConfigRepository.findAll().stream().findFirst().orElse(null);
        if (config == null)
            throw new RuntimeException("Payment configuration not found");

        if ("store1".equals(config.getStoreId())) {
            return "http://localhost:8081/api/donation/success?tran_id=" + donation.getTransactionId();
        }

        return sendPaymentRequest(config, donation.getAmount(), donation.getTransactionId(), donation.getDonorName(),
                donation.getPhone(), "Donation", "http://localhost:8081/api/donation/success");
    }

    private String sendPaymentRequest(PaymentConfig config, Double amount, String tranId, String cusName,
            String cusPhone, String product, String successUrl) {
            
        String baseUrl = config.isLive() ? "https://securepay.sslcommerz.com" : "https://sandbox.sslcommerz.com";
        String initiateUrl = baseUrl + "/gwprocess/v4/api.php";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("store_id", config.getStoreId());
        map.add("store_passwd", config.getStorePassword());
        map.add("total_amount", String.valueOf(amount));
        map.add("currency", "BDT");
        map.add("tran_id", tranId);
        map.add("success_url", successUrl);
        map.add("fail_url", "http://localhost:8081/api/payment/fail");
        map.add("cancel_url", "http://localhost:8081/api/payment/cancel");
        map.add("cus_name", cusName);
        map.add("cus_email", "donor@example.com");
        map.add("cus_add1", "Dhaka");
        map.add("cus_add2", "Dhaka");
        map.add("cus_city", "Dhaka");
        map.add("cus_state", "Dhaka");
        map.add("cus_postcode", "1000");
        map.add("cus_country", "Bangladesh");
        map.add("cus_phone", cusPhone);
        map.add("shipping_method", "NO");
        map.add("product_name", product);
        map.add("product_category", "Donation");
        map.add("product_profile", "general");

        try {
            Map<String, Object> response = restTemplate.postForObject(initiateUrl, map, Map.class);
            if (response != null && "SUCCESS".equals(response.get("status"))) {
                return (String) response.get("GatewayPageURL");
            } else {
                throw new RuntimeException("Payment initiation failed: "
                        + (response != null ? response.get("failedreason") : "Unknown error"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Payment initiation failed: " + e.getMessage());
        }
    }

    public boolean validatePayment(String valId) {
        PaymentConfig config = paymentConfigRepository.findAll().stream().findFirst().orElse(null);
        if (config == null) {
            return false;
        }

        String baseUrl = config.isLive() ? "https://securepay.sslcommerz.com" : "https://sandbox.sslcommerz.com";
        String validateUrl = baseUrl + "/validator/api/validationserverAPI.php?val_id=" + valId + "&store_id="
                + config.getStoreId() + "&store_passwd=" + config.getStorePassword() + "&format=json";

        try {
            Map<String, Object> response = restTemplate.getForObject(validateUrl, Map.class);
            return response != null
                    && ("VALID".equals(response.get("status")) || "VALIDATED".equals(response.get("status")));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
