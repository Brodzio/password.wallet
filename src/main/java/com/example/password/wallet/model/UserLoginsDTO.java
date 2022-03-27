package com.example.password.wallet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserLoginsDTO {

    private Timestamp lastLoginDate;
    private boolean isCorrectLogin;
}
