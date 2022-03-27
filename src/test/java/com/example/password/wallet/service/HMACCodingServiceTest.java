package com.example.password.wallet.service;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class HMACCodingServiceTest {

    @Test
    public void testCalculateHMAC() {
        System.out.println("testCalculateHMAC");
        String expected = "Rx9gm/bYsNZBnsaO/VRTs5IlYKo/NRCI41pCTDDkNyXCYfjmMfNMsGykda5niwqhm1sMdpDf8wsNiOlqB3ID9Q==";
        String result = HMACCodingService.calculateHMAC("test", "1234");
        assertEquals(result, expected);
    }
}