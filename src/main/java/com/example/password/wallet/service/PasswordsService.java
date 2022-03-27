package com.example.password.wallet.service;

import com.example.password.wallet.dbmodels.Password;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.model.PasswordDTO;
import com.example.password.wallet.model.PasswordToActionDTO;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.repository.PasswordRepository;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordsService {

    private final UserRepository userRepository;
    private final LoginService loginService;
    private final PasswordRepository passwordRepository;

    public void addPasswordToTheWallet(PasswordDTO passwordDTO, UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        loginService.checkUserCredentials(userDTO);

        Key key = AESCodingService.generateKey(userDTO.getPassword());
        Password password = null;
        try {
            password = Password.builder()
                    .login(passwordDTO.getLogin())
                    .password(AESCodingService.encrypt(passwordDTO.getPassword(), key))
                    .webAddress(passwordDTO.getUrl())
                    .user(user)
                    .build();

            passwordRepository.save(password);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updatePassword(PasswordToActionDTO password, UserDTO userDTO) throws Exception {
        Key key = AESCodingService.generateKey(userDTO.getPassword());
        password.setPassword(AESCodingService.encrypt(password.getPassword(), key));
        passwordRepository.updatePasswordFromWallet(
                password.getId(),
                password.getLogin(),
                password.getPassword(),
                password.getWebAddress()
        );
    }

    public List<Password> getAllUserPassword(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        loginService.checkUserCredentials(userDTO);

        return passwordRepository.findAllByUser(user.getId());
    }

    public List<PasswordToActionDTO> getAllDecryptedPassword(List<Password> passwords, UserDTO userDTO) throws Exception {

        List<PasswordToActionDTO> decryptedPasswords = new ArrayList<>();

        for(Password pass : passwords) {
            String encryptedPassword = pass.getPassword();

            Key key = AESCodingService.generateKey(userDTO.getPassword());
            String decryptedPassword = null;
            try {
                decryptedPassword = AESCodingService.decrypt(encryptedPassword, key);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            PasswordToActionDTO decryptedPass = new PasswordToActionDTO();
            decryptedPass.setId(pass.getId());
            decryptedPass.setLogin(pass.getLogin());
            decryptedPass.setPassword(decryptedPassword);
            decryptedPass.setWebAddress(pass.getWebAddress());

            decryptedPasswords.add(decryptedPass);
        }
        return decryptedPasswords;
    }

    public void deletePassword(PasswordToActionDTO password) {
        passwordRepository.deleteById(password.getId());
    }
}
