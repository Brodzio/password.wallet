package com.example.password.wallet.service;

import com.example.password.wallet.config.EncodingConfiguration;
import com.example.password.wallet.dbmodels.Password;
import com.example.password.wallet.dbmodels.SharedPassword;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.model.PasswordDTO;
import com.example.password.wallet.model.PasswordToShareDTO;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.repository.SharedPasswordRepository;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SharedPasswordService {

    private final UserRepository userRepository;
    private final EncodingConfiguration config;
    private final SharedPasswordRepository sharedPasswordRepository;

    public void sharePasswordToUserByEmail(PasswordToShareDTO passwordToShareDTO) throws Exception {
        User user = userRepository.findByEmail(passwordToShareDTO.getEmail());

        Key key = AESCodingService.generateKey(config.getPepper());
        SharedPassword password = null;

        try {
            password = SharedPassword.builder()
                    .login(passwordToShareDTO.getLogin())
                    .password(AESCodingService.encrypt(passwordToShareDTO.getPassword(), key))
                    .webAddress(passwordToShareDTO.getWebAddress())
                    .user(user)
                    .build();

            sharedPasswordRepository.save(password);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<SharedPassword> getAllSharedPassword(UserDTO userDTO) {
        User user = userRepository.findByLogin(userDTO.getLogin());

        return sharedPasswordRepository.findAllByUser(user.getId());
    }

    public List<PasswordDTO> getAllDecryptedPassword(List<SharedPassword> passwords, UserDTO userDTO) throws Exception {

        List<PasswordDTO> decryptedPasswords = new ArrayList<>();

        for(SharedPassword pass : passwords) {
            String encryptedPassword = pass.getPassword();

            Key key = AESCodingService.generateKey(config.getPepper());
            String decryptedPassword = null;
            try {
                decryptedPassword = AESCodingService.decrypt(encryptedPassword, key);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            PasswordDTO decryptedPass = new PasswordDTO();
            decryptedPass.setLogin(pass.getLogin());
            decryptedPass.setPassword(decryptedPassword);
            decryptedPass.setUrl(pass.getWebAddress());

            decryptedPasswords.add(decryptedPass);
        }
        return decryptedPasswords;
    }
}
