package com.petscare.controller;

import com.petscare.entity.Order;
import com.petscare.entity.Product;
import com.petscare.repository.OrderRepository;
import com.petscare.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepository.findAllByOrderByIdDesc();
    }

    @Autowired
    private com.petscare.service.SSLCommerzService sslCommerzService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> payload) {
        try {
            Order order = new Order();
            order.setCustomerName((String) payload.get("name"));
            order.setPhone((String) payload.get("phone"));
            order.setLocation((String) payload.get("location"));
            order.setItems((String) payload.get("items"));

            String paymentMethod = (String) payload.get("paymentMethod");
            order.setPaymentMethod(paymentMethod);
            order.setTransactionId("TXN_" + System.currentTimeMillis());

            // Handle total as Double or String
            Object totalObj = payload.get("total");
            Double total = 0.0;
            if (totalObj instanceof String) {
                total = Double.parseDouble((String) totalObj);
            } else if (totalObj instanceof Number) {
                total = ((Number) totalObj).doubleValue();
            }
            order.setTotalAmount(total);

            if ("SSLCommerz".equals(paymentMethod)) {
                order.setPaymentStatus("Pending");
                order.setStatus("Pending Payment");
                orderRepository.save(order);
                String redirectUrl = sslCommerzService.initiatePayment(order);
                return ResponseEntity
                        .ok(Map.of("success", true, "redirectUrl", redirectUrl, "paymentMethod", "SSLCommerz"));
            } else {
                order.setPaymentStatus("Pending (COD)");
                order.setStatus("Pending");
                orderRepository.save(order);
                return ResponseEntity.ok(Map.of("success", true, "paymentMethod", "COD"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
