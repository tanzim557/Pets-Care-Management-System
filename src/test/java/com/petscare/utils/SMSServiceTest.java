package com.petscare.utils;

import org.junit.jupiter.api.Test;

public class SMSServiceTest {

    @Test
    public void testSendSMS() {
        // Using a dummy number to test the API response
        // The API should respond with an error or success depending on the key/senderId
        // validity
        // But we just want to see the response in the console.
        SMSService.sendSMS("01700000000", "Test Message from PetsCare");
    }
}
