package com.example.password.wallet.service;

import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.dbmodels.UserLogins;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.model.UserLoginsDTO;
import com.example.password.wallet.repository.UserLoginsRepository;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginsService {

    private final UserLoginsRepository userLoginsRepository;
    private final UserRepository userRepository;
    private final LoginService loginService;
    private final Integer correct = 1;
    private final Integer wrong = 0;
    private Integer count;

    public List<UserLoginsDTO> getAllUserLoginsById(UserDTO userDTO) throws Exception {

        List<UserLoginsDTO> userLoginsDTOList = new ArrayList<>();

        UserLoginsDTO correct = new UserLoginsDTO();

        UserLogins correctUserLogin = getLastCorrectUserLogin(userDTO);
        UserLogins wrongUserLogin = getLastWrongUserLogin(userDTO);

        correct.setLastLoginDate(correctUserLogin.getLastLoginDate());
        correct.setCorrectLogin(correctUserLogin.isCorrectLogin());

        userLoginsDTOList.add(correct);

        UserLoginsDTO wrong = new UserLoginsDTO();

        wrong.setLastLoginDate(wrongUserLogin.getLastLoginDate());
        wrong.setCorrectLogin(wrongUserLogin.isCorrectLogin());

        userLoginsDTOList.add(wrong);

        return userLoginsDTOList;
    }

    public Integer countWrongUserLoginAttempts(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        loginService.checkUserCredentials(userDTO);

        List<UserLogins> userLogins = userLoginsRepository.findAllByUser(user.getId());

        if(!userLogins.get(1).isCorrectLogin()) {
            count = 1;

            for(int i = 2; i < userLogins.size(); i++) {
                if (Boolean.FALSE.equals(userLogins.get(i).isCorrectLogin())) {
                    count++;
                }
                if (Boolean.TRUE.equals(userLogins.get(i).isCorrectLogin())) {
                    break;
                }
            }

            return count;
        }

        return 0;
    }

    public UserLogins getLastCorrectUserLogin(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        loginService.checkUserCredentials(userDTO);

        UserLogins userLogins = userLoginsRepository.findLastCorrectUserLoginByUserId(user.getId(), correct);

        return userLogins;
    }

    public UserLogins getLastWrongUserLogin(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        loginService.checkUserCredentials(userDTO);

        UserLogins userLogins = userLoginsRepository.findLastWrongUserLoginByUserId(user.getId(), wrong);

        return userLogins;
    }
}
