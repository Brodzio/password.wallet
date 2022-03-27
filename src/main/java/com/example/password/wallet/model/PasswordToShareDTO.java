package com.example.password.wallet.model;

import lombok.Data;

@Data
public class PasswordToShareDTO {
    private String email;
    private String login;
    private String password;
    private String webAddress;
}
