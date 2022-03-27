package com.example.password.wallet.service;

import com.example.password.wallet.config.EncodingConfiguration;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.model.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SHA512CodingService {

    private final EncodingConfiguration config;

    public User encodeHashValue(UserDTO userDTO, String salt) {
        if(Objects.isNull((salt))) {
            salt = generateSalt();
        }

        String encodedSHAPassword = calculateSHA512(userDTO.getPassword() + salt);

        return User.builder()
                .login(userDTO.getLogin())
                .passwordHash(encodedSHAPassword)
                .salt(salt)
                .isPasswordKeptAsHash(userDTO.getKeepPasswordAsHash())
                .email(userDTO.getEmail())
                .build();
    }

    private String generateSalt() {
        int length = config.getSalt().getLength();
        boolean useLetters = config.getSalt().getUseLetters();
        boolean useNumbers = config.getSalt().getUseNumbers();

        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    private static String calculateSHA512(String text) {
        try {
            //get an instance of SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            // calculate message digest of the input string
            byte[] messageDigest = md.digest(text.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            //return the HashText
            return hashtext;
        }
        // If wrong message digest algorithm was specified
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
