package com.example.password.wallet.model;

import lombok.Data;

@Data
public class PasswordToActionDTO {
    private Long id;
    private String login;
    private String password;
    private String webAddress;
}
