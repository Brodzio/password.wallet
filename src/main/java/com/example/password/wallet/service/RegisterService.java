package com.example.password.wallet.service;

import com.example.password.wallet.config.EncodingConfiguration;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterService {

    private final UserRepository userRepository;
    private final SHA512CodingService sha512CodingService;
    private final EncodingConfiguration config;

    public void registerUser(UserDTO userDTO) throws Exception {
        if(Boolean.TRUE.equals(userDTO.getKeepPasswordAsHash())){
            registerUserWithHashPassword(userDTO);
        } else {
            registerUserWithoutHashPassword(userDTO);
        }
    }

    private void registerUserWithHashPassword(UserDTO userDTO) throws Exception {
        checkIfUserExists(userDTO.getLogin());
        User user = sha512CodingService.encodeHashValue(userDTO, null);
        Key key = AESCodingService.generateKey(config.getPepper());
        user.setPasswordHash(AESCodingService.encrypt(user.getPasswordHash(), key));
        userRepository.save(user);
    }

    private void registerUserWithoutHashPassword(UserDTO userDTO) throws Exception {
        checkIfUserExists(userDTO.getLogin());
        User user = sha512CodingService.encodeHashValue(userDTO, null);
        user.setPasswordHash(HMACCodingService.calculateHMAC(user.getPasswordHash(),config.getPepper()));
        userRepository.save(user);
    }

    private void checkIfUserExists(String login) throws Exception {
        if(userRepository.existsByLogin(login)){
            throw new Exception();
        }
    }
}
