package com.example.password.wallet.service;

import com.example.password.wallet.repository.UserLoginsRepository;
import mockit.Injectable;
import mockit.Tested;
import org.junit.runner.RunWith;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LoginServiceTest {

    @Test
    public void testAddLoginAttemptInfo() {
        System.out.println("testAddLoginAttemptInfo");
        int a = 12;
        int b = 6;
        int wynik = a/b;
        int c = 2;
        assertEquals(c, wynik);
    }

    @Test
    public void testAddIpAddressInfo() {
        System.out.println("testAddIpAddressInfo");
        int a = 12;
        int b = 6;
        int wynik = a/b;
        int c = 2;
        assertEquals(c, wynik);
    }
}