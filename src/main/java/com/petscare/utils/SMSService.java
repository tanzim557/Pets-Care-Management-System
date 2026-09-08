package com.petscare.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SMSService {
    private static final String API_URL = "http://bulksmsbd.net/api/smsapi";
    private static String apiKey;
    private static String senderId;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        // Try environment variables first (for Docker deployment)
        apiKey = System.getenv("SMS_API_KEY");
        senderId = System.getenv("SMS_SENDER_ID");

        // Fall back to properties file if environment variables not set
        if (apiKey == null || senderId == null) {
            Properties prop = new Properties();
            try (FileInputStream input = new FileInputStream("sms_config.properties")) {
                prop.load(input);
                if (apiKey == null) {
                    apiKey = prop.getProperty("api_key");
                }
                if (senderId == null) {
                    senderId = prop.getProperty("sender_id");
                }
                System.out.println("ℹ️  Loaded SMS config from sms_config.properties");
            } catch (IOException ex) {
                System.err.println("⚠️  Could not load sms_config.properties: " + ex.getMessage());
                System.err.println("ℹ️  Set SMS_API_KEY and SMS_SENDER_ID environment variables for Docker deployment");
            }
        } else {
            System.out.println("ℹ️  Loaded SMS config from environment variables");
        }
    }

    public static void sendSMS(String phone, String message) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════");
        System.out.println("║ 📱 SMS SERVICE - BULK SMS BD");
        System.out.println("║═══════════════════════════════════════════════════════════════════════════");
        System.out.println("║ 📞 Phone       │ " + phone);
        System.out.println("║ 💬 Message     │ " + message);
        System.out.println("║ 🔑 Sender ID   │ " + (senderId != null ? senderId : "NOT CONFIGURED"));

        if (apiKey == null || senderId == null) {
            System.out.println("║─────────────────────────────────────────────────────────────────────────");
            System.out.println("║ ❌ ERROR       │ SMS Configuration missing. Check sms_config.properties");
            System.out.println("╚═══════════════════════════════════════════════════════════════════════════");
            System.out.println("");
            return;
        }

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String urlParameters = "api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                    "&senderid=" + URLEncoder.encode(senderId, StandardCharsets.UTF_8) +
                    "&number=" + URLEncoder.encode(phone, StandardCharsets.UTF_8) +
                    "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

            System.out.println("║─────────────────────────────────────────────────────────────────────────");
            System.out.println("║ 🌐 API Endpoint│ " + API_URL);
            System.out.println("║ 📡 Sending...  │ POST Request");

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            String statusIcon = responseCode >= 200 && responseCode < 300 ? "✅" : "❌";

            System.out.println("║─────────────────────────────────────────────────────────────────────────");
            System.out.println("║ " + statusIcon + " HTTP Status │ " + responseCode);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("║ 📥 API Response│ " + response.toString());
            }

            System.out.println("╚═══════════════════════════════════════════════════════════════════════════");
            System.out.println("");

        } catch (Exception e) {
            System.out.println("║─────────────────────────────────────────────────────────────────────────");
            System.out.println("║ ❌ EXCEPTION   │ " + e.getMessage());
            System.out.println("╚═══════════════════════════════════════════════════════════════════════════");
            System.out.println("");
            e.printStackTrace();
        }
    }
}
