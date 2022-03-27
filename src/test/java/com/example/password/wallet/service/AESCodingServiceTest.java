package com.example.password.wallet.service;

import static org.testng.Assert.*;

import com.example.password.wallet.config.EncodingConfiguration;
import org.testng.annotations.Test;

import java.security.Key;

public class AESCodingServiceTest {

    @Test()
    public void testEncrypt() throws Exception {
        System.out.println("testEncryptAESCoding");
        String expected = "XDngx/P9tFHrd4DP3D1I/g==";
        String result = AESCodingService.encrypt("test", AESCodingService.generateKey("123456"));
        assertEquals(result, expected);
    }

    @Test
    public void testDecrypt() throws Exception {
        System.out.println("testDecryptAESCoding");
        String expected = "test";
        String result = AESCodingService.decrypt("XDngx/P9tFHrd4DP3D1I/g==", AESCodingService.generateKey("123456"));
        assertEquals(result, expected);
    }

    @Test
    public void testGenerateKey() throws Exception {
        System.out.println("testGenerateKey");
        Key expected = AESCodingService.generateKey("test");
        Key result = AESCodingService.generateKey("test");
        assertEquals(expected, result);
    }
}