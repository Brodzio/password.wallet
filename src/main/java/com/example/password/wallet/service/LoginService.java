package com.example.password.wallet.service;

import com.example.password.wallet.config.EncodingConfiguration;
import com.example.password.wallet.dbmodels.IPLogins;
import com.example.password.wallet.dbmodels.User;
import com.example.password.wallet.dbmodels.UserLogins;
import com.example.password.wallet.model.UserDTO;
import com.example.password.wallet.repository.IPLoginsRepository;
import com.example.password.wallet.repository.UserLoginsRepository;
import com.example.password.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final UserLoginsRepository userLoginsRepository;
    private final IPLoginsRepository ipLoginsRepository;
    private final SHA512CodingService sha512CodingService;
    private final EncodingConfiguration config;

    public void checkUserCredentials(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        if(Objects.isNull(user)) {
            throw new Exception();
        }

        User tempUser = sha512CodingService.encodeHashValue(userDTO, user.getSalt());

        if(user.getIsPasswordKeptAsHash()) {
            Key key = AESCodingService.generateKey(config.getPepper());
            tempUser.setPasswordHash(AESCodingService.encrypt(tempUser.getPasswordHash(), key));
            if(!user.getPasswordHash().equals(tempUser.getPasswordHash())) {
                throw new Exception();
            }
        } else {
            String hmacTempUserPassword = HMACCodingService.calculateHMAC(tempUser.getPasswordHash(), config.getPepper());
            if(!user.getPasswordHash().equals(hmacTempUserPassword)) {
                throw new Exception();
            }
        }
    }

    public boolean checkUsersLogin(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        if(Objects.isNull(user)) {
            throw new Exception();
        }

        User tempUser = sha512CodingService.encodeHashValue(userDTO, user.getSalt());

        if(user.getIsPasswordKeptAsHash()) {
            Key key = AESCodingService.generateKey(config.getPepper());
            tempUser.setPasswordHash(AESCodingService.encrypt(tempUser.getPasswordHash(), key));
            if(!user.getPasswordHash().equals(tempUser.getPasswordHash())) {
                addLoginAttemptInfo(user, false);
                addIpAddressInfo(user, false);
                return false;
            } else {
                addLoginAttemptInfo(user, true);
                addIpAddressInfo(user, true);
                return true;
            }
        } else {
            String hmacTempUserPassword = HMACCodingService.calculateHMAC(tempUser.getPasswordHash(), config.getPepper());
            if(!user.getPasswordHash().equals(hmacTempUserPassword)) {
                addLoginAttemptInfo(user, false);
                addIpAddressInfo(user, false);
                return false;
            } else {
                addLoginAttemptInfo(user, true);
                addIpAddressInfo(user, true);
                return true;
            }
        }
    }

    public void addLoginAttemptInfo(User user, boolean isCorrect) {
        UserLogins userLogins = new UserLogins();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        userLogins.setLastLoginDate(timestamp);
        userLogins.setCorrectLogin(isCorrect);
        userLogins.setUser(user);
        userLoginsRepository.save(userLogins);
    }

    public void addIpAddressInfo(User user, boolean isCorrect) throws SocketException {
        IPLogins ipLogins = new IPLogins();

        String ip = "";

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ipLogins.setIpAddress(ip);

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        ipLogins.setLastLoginDate(timestamp);
        ipLogins.setCorrectLogin(isCorrect);
        ipLogins.setUser(user);
        ipLoginsRepository.save(ipLogins);
    }

    public boolean checkIpAddressToBlockAccount(UserDTO userDTO) {
        User user = userRepository.findByLogin(userDTO.getLogin());

        List<IPLogins> ipLogins = ipLoginsRepository.findAllIpLoginsByUserId(user.getId());

        int count = 0;
        if(!ipLogins.isEmpty()) {
            if(!ipLogins.get(0).isCorrectLogin()) {
                count = 1;

                for(int i = 1; i < ipLogins.size(); i++) {
                    if (Boolean.FALSE.equals(ipLogins.get(i).isCorrectLogin())) {
                        count++;
                    }
                    if (Boolean.TRUE.equals(ipLogins.get(i).isCorrectLogin())) {
                        break;
                    }
                }
            }
        } else {
            return true;
        }

        System.out.println(count);

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        Long time = (timestamp.getTime()-ipLogins.get(0).getLastLoginDate().getTime())/1000;
        System.out.println(time);

        if(count == 20) {
            if(time < 1800) {
                System.out.println("blokada ip");
                return false;
            }
        }

        return true;
    }

    public boolean checkAttemptsToBlockAccount(UserDTO userDTO) throws Exception {
        User user = userRepository.findByLogin(userDTO.getLogin());

        List<UserLogins> userLogins = userLoginsRepository.findAllByUser(user.getId());

        int count = 0;
        if(!userLogins.isEmpty()) {
            if (!userLogins.get(0).isCorrectLogin()) {
                count = 1;

                for (int i = 1; i < userLogins.size(); i++) {
                    if (Boolean.FALSE.equals(userLogins.get(i).isCorrectLogin())) {
                        count++;
                    }
                    if (Boolean.TRUE.equals(userLogins.get(i).isCorrectLogin())) {
                        break;
                    }
                }
            }
        } else {
            return true;
        }

        System.out.println(count);

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        Long time = (timestamp.getTime()-userLogins.get(0).getLastLoginDate().getTime())/1000;
        System.out.println(time);

        switch (count) {
            case 3:
                if(time < 5) {
                    System.out.println("pierwsza blokada");
                    return false;
                }
                break;
            case 4:
                if(time < 10) {
                    System.out.println("druga blokada");
                    return false;
                }
                break;
            case 5:
                if(time < 120) {
                    System.out.println("trzecia blokada");
                    return false;
                }
                break;
            case 10:
                if(time < 1800) {
                    System.out.println("czwarta blokada");
                    return false;
                }
                break;
            default:
                return true;
        }

        return true;
    }
}
