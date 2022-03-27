package com.example.password.wallet.service;

import com.example.password.wallet.dbmodels.IPLogins;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.repository.IPLoginsRepository;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpLoginsService {

    private final UserRepository userRepository;
    private final IPLoginsRepository ipLoginsRepository;

    public List<String> getListOfDistinctIp(UserDTO userDTO) {

        User user = userRepository.findByLogin(userDTO.getLogin());
        List<String> ipList = ipLoginsRepository.getIpListByUserId(user.getId());

        return ipList;
    }

    public void unblockIpAddress(UserDTO userDTO, String ipAddress) {
        User user = userRepository.findByLogin(userDTO.getLogin());
        IPLogins ipLogins = new IPLogins();
        ipLogins.setIpAddress(ipAddress);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        ipLogins.setLastLoginDate(timestamp);
        ipLogins.setCorrectLogin(true);
        ipLogins.setUser(user);
        ipLoginsRepository.save(ipLogins);
    }

}
