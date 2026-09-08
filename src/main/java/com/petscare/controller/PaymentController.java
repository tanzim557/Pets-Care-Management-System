package com.petscare.controller;

import com.petscare.entity.Order;
import com.petscare.entity.PaymentConfig;
import com.petscare.repository.OrderRepository;
import com.petscare.repository.PaymentConfigRepository;
import com.petscare.service.SSLCommerzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private SSLCommerzService sslCommerzService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentConfigRepository paymentConfigRepository;

    @RequestMapping(value = "/success", method = { RequestMethod.GET, RequestMethod.POST })
    public void paymentSuccess(@RequestParam Map<String, String> requestParams, HttpServletResponse response) {
        try {
            System.out.println("=== PAYMENT SUCCESS CALLBACK RECEIVED ===");
            System.out.println("Parameters: " + requestParams);

            String tranId = requestParams.get("tran_id");
            String valId = requestParams.get("val_id");

            System.out.println("Transaction ID: " + tranId);
            System.out.println("Validation ID: " + valId);

            if (tranId == null) {
                System.out.println("ERROR: Missing transaction ID");
                response.sendRedirect("/shop.html?error=MissingParameters");
                return;
            }

            // Find order by transaction ID
            Order order = orderRepository.findAll().stream()
                    .filter(o -> tranId.equals(o.getTransactionId()))
                    .findFirst()
                    .orElse(null);

            if (order != null) {
                System.out.println("Order found: ID=" + order.getId() + ", Current Status=" + order.getPaymentStatus());

                // Update payment status to Paid
                order.setPaymentStatus("Paid");
                order.setStatus("Paid");
                orderRepository.save(order);

                System.out.println("Order updated successfully - PaymentStatus=Paid, OrderStatus=Processing");
                response.sendRedirect("/thank_you.html?type=order");
            } else {
                System.out.println("ERROR: Order not found for transaction: " + tranId);
                response.sendRedirect("/shop.html?error=OrderNotFound");
            }
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR in payment success handler: " + e.getMessage());
            e.printStackTrace();
            try {
                response.sendRedirect("/shop.html?error=SystemError");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/fail", method = { RequestMethod.GET, RequestMethod.POST })
    public void paymentFail(@RequestParam Map<String, String> requestParams, HttpServletResponse response)
            throws Exception {
        String tranId = requestParams.get("tran_id");
        Order order = orderRepository.findAll().stream()
                .filter(o -> tranId.equals(o.getTransactionId()))
                .findFirst()
                .orElse(null);

        if (order != null) {
            order.setPaymentStatus("Failed");
            orderRepository.save(order);
        }
        response.sendRedirect("/shop.html?error=PaymentFailed");
    }

    @RequestMapping(value = "/cancel", method = { RequestMethod.GET, RequestMethod.POST })
    public void paymentCancel(@RequestParam Map<String, String> requestParams, HttpServletResponse response)
            throws Exception {
        String tranId = requestParams.get("tran_id");
        Order order = orderRepository.findAll().stream()
                .filter(o -> tranId.equals(o.getTransactionId()))
                .findFirst()
                .orElse(null);

        if (order != null) {
            order.setPaymentStatus("Cancelled");
            orderRepository.save(order);
        }
        response.sendRedirect("/shop.html?error=PaymentCancelled");
    }

    @GetMapping("/settings")
    public ResponseEntity<PaymentConfig> getSettings() {
        return ResponseEntity.ok(paymentConfigRepository.findAll().stream().findFirst().orElse(new PaymentConfig()));
    }

    @PostMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody PaymentConfig config) {
        paymentConfigRepository.deleteAll();
        paymentConfigRepository.save(config);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/test/latest-order")
    public ResponseEntity<?> getLatestOrder() {
        Order latest = orderRepository.findAll().stream()
                .reduce((first, second) -> second)
                .orElse(null);
        if (latest == null) {
            return ResponseEntity.ok(Map.of("message", "No orders found"));
        }
        return ResponseEntity.ok(Map.of(
                "id", latest.getId(),
                "customerName", latest.getCustomerName(),
                "transactionId", latest.getTransactionId(),
                "paymentMethod", latest.getPaymentMethod(),
                "paymentStatus", latest.getPaymentStatus(),
                "status", latest.getStatus()));
    }
}
