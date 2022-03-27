package com.example.password.wallet.controller;

import com.example.password.wallet.dbmodels.*;
import com.example.password.wallet.model.*;
import com.example.password.wallet.repository.UserRepository;
import com.example.password.wallet.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private UserDTO currentUserDto = null;
    private final PasswordsService passwordsService;
    private final ChangePasswordService changePasswordService;
    private final UserLoginsService userLoginsService;
    private final IpLoginsService ipLoginsService;
    private final SharedPasswordService sharedPasswordService;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("register")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new UserDTO());

        return "signup_form";
    }

    @PostMapping("/register")
    public String registerUser(UserDTO user) throws Exception {
        registerService.registerUser(user);

        return "redirect:/login";
    }

    @GetMapping("login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserDTO());

        return "login_form";
    }

    @PostMapping("/login")
    public String logInUser(UserDTO user) throws Exception {
        if(loginService.checkIpAddressToBlockAccount(user)) {
            if (loginService.checkAttemptsToBlockAccount(user)) {
                if (loginService.checkUsersLogin(user)) {
                    currentUserDto = user;
                    return "menu";
                } else {
                    return "redirect:/login";
                }
            } else {
                return "time_block";
            }
        } else {
            return "ip_block";
        }
    }

    @GetMapping("logout")
    public String showAddForm() {

        currentUserDto = null;

        return "index";
    }

    @GetMapping("add")
    public String addNewPasswordToWallet(Model model) {
        model.addAttribute("password", new PasswordDTO());

        return "password_form";
    }

    @PostMapping("/add_password")
    public String saveNewPassword(PasswordDTO passwordDTO) throws Exception {

        passwordsService.addPasswordToTheWallet(passwordDTO, currentUserDto);

        return "menu";
    }

    @GetMapping("getAllUserPasswords")
    public String getAllUserPassword(Model model) throws Exception {
        List<Password> passwords = passwordsService.getAllUserPassword(currentUserDto);
        model.addAttribute("passwords", passwords);

        return "passwords";
    }

    @GetMapping("lastLogins")
    public String lastLogins(Model model) throws Exception {
        //UserLogins userLogins = userLoginsService.getLastCorrectUserLogin(currentUserDto);
        //UserLogins userLogins = userLoginsService.getLastWrongUserLogin(currentUserDto);
        List<UserLoginsDTO> userLogins = userLoginsService.getAllUserLoginsById(currentUserDto);
        Integer temp = userLoginsService.countWrongUserLoginAttempts(currentUserDto);
        System.out.println(temp);
        model.addAttribute("userLogins", userLogins);
        model.addAttribute("counts", temp);

        return "user_logins";
    }

    @GetMapping("ipList")
    public String ipListLogins(Model model) {
        List<String> ipList = ipLoginsService.getListOfDistinctIp(currentUserDto);
        model.addAttribute("ipList", ipList);

        return "ip_list";
    }

    @PostMapping("ipList")
    public String ipListLoginsUnblock(@ModelAttribute(value = "ipToUnblock") IpDTO ipDTO) {
        ipLoginsService.unblockIpAddress(currentUserDto, ipDTO.getIpAddress());
        System.out.println("To jest ip: " + ipDTO.getIpAddress());

        return "ip_list";
    }

    @PostMapping("decrypt_passwords")
    public String getAllUserDecryptedPasswords(Model model) throws Exception {
        List<Password> passwords = passwordsService.getAllUserPassword(currentUserDto);

        List<PasswordToActionDTO> decryptedPasswords = passwordsService.getAllDecryptedPassword(passwords, currentUserDto);

        model.addAttribute("passwords", decryptedPasswords);

        return "decrypted_passwords";
    }

    @PostMapping("sharePassword")
    public String showSendingForm(@ModelAttribute(value = "sharedPassword") PasswordToShareDTO passwordDTO, Model model) {
        model.addAttribute("passwordToShare", passwordDTO);
        System.out.println("1"+passwordDTO);

        return "shareForm";
    }

    @PostMapping("sharingPassword")
    public String sendPasswordByEmail(@ModelAttribute(value = "passwordToShare") PasswordToShareDTO passwordToShareDTO, Model model) throws Exception {
        System.out.println("2"+passwordToShareDTO);

        sharedPasswordService.sharePasswordToUserByEmail(passwordToShareDTO);

        List<Password> passwords = passwordsService.getAllUserPassword(currentUserDto);
        model.addAttribute("passwords", passwords);

        return "passwords";
    }

    @GetMapping("getAllSharedPasswords")
    public String getAllSharedPasswords(Model model) {
        List<SharedPassword> passwords = sharedPasswordService.getAllSharedPassword(currentUserDto);
        model.addAttribute("sharedPasswords", passwords);

        return "sharedPasswords";
    }

    @PostMapping("decrypt_shared_passwords")
    public String getAllSharedDecryptedPasswords(Model model) throws Exception {
        List<SharedPassword> passwords = sharedPasswordService.getAllSharedPassword(currentUserDto);

        List<PasswordDTO> decryptedPasswords = sharedPasswordService.getAllDecryptedPassword(passwords, currentUserDto);

        model.addAttribute("passwords", decryptedPasswords);

        return "decrypted_shared_passwords";
    }

    @RequestMapping("/back")
    public String back() {
        return "menu";
    }

    @GetMapping("changePassword")
    public String changeUserPassword(Model model) {
        model.addAttribute("changePassword", new ChangePasswordDTO());

        return "change_password_form";
    }

    @PostMapping("changeUserPassword")
    public String changeUserPasswords(ChangePasswordDTO changePasswordDTO) throws Exception {
        changePasswordService.changeUserPassword(changePasswordDTO, currentUserDto);

        return "index";
    }

    @PostMapping("deletePassword")
    public String deleteUserPassword(@ModelAttribute(value = "deletedPassword") PasswordToActionDTO password, Model model) throws Exception {
        passwordsService.deletePassword(password);

        List<Password> passwords = passwordsService.getAllUserPassword(currentUserDto);
        model.addAttribute("passwords", passwords);

        return "passwords";
    }

    @PostMapping("updatePassword")
    public String updatePassword(@ModelAttribute(value = "updatedPassword") PasswordToActionDTO password, Model model) {
        model.addAttribute("passwordToUpdate", password);
        System.out.println("pierwsze" + password);

        return "updatePasswordForm";
    }

    @PostMapping("updatingPassword")
    public String saveUpdatedPassword(@ModelAttribute(value = "passwordToUpdate") PasswordToActionDTO password, Model model) throws Exception {
        System.out.println(password);
        passwordsService.updatePassword(password, currentUserDto);

        List<Password> passwords = passwordsService.getAllUserPassword(currentUserDto);
        model.addAttribute("passwords", passwords);

        return "passwords";
    }
}
