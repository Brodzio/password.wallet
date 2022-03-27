package com.example.password.wallet.service;

import com.example.password.wallet.config.EncodingConfiguration;
import com.example.password.wallet.dbmodels.Password;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.model.ChangePasswordDTO;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.repository.PasswordRepository;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Key;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final LoginService loginService;
    private final SHA512CodingService sha512CodingService;
    private final EncodingConfiguration config;
    private final PasswordRepository passwordRepository;

    @Transactional
    public void changeUserPassword(ChangePasswordDTO changePasswordDTO, UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        loginService.checkUserCredentials(
                new UserDTO(
                        userDTO.getLogin(),
                        changePasswordDTO.getPassword(),
                        user.getIsPasswordKeptAsHash(),
                        userDTO.getEmail()
                )
        );

        User newUser = sha512CodingService.encodeHashValue(
                new UserDTO(
                        userDTO.getLogin(),
                        changePasswordDTO.getNewPassword(),
                        changePasswordDTO.getKeepAsHash(),
                        userDTO.getEmail()
                ),
                null
        );

        if(changePasswordDTO.getKeepAsHash()) {
            Key key = AESCodingService.generateKey(config.getPepper());
            newUser.setPasswordHash(AESCodingService.encrypt(newUser.getPasswordHash(), key));
            System.out.println(newUser);
            userRepository.updateUserDataWithNewPassword(newUser.getIsPasswordKeptAsHash(), newUser.getPasswordHash(), newUser.getSalt(), user.getId());
        } else {
            String newPasswordHash = HMACCodingService.calculateHMAC(newUser.getPasswordHash(), config.getPepper());
            userRepository.updateUserDataWithNewPassword(newUser.getIsPasswordKeptAsHash(), newPasswordHash, newUser.getSalt(), user.getId());
        }

        List<Password> passwordsList = passwordRepository.findAllByUser(user.getId());

        for (Password passwordInList : passwordsList) {
            Key key = AESCodingService.generateKey(changePasswordDTO.getPassword());
            String password = AESCodingService.decrypt(passwordInList.getPassword(), key);
            key = AESCodingService.generateKey(changePasswordDTO.getNewPassword());
            String encryptedPassword = AESCodingService.encrypt(password, key);
            passwordRepository.updatePasswordDataWithNewPassword(encryptedPassword, passwordInList.getId());
        }
    }
}
