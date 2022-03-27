package com.example.password.wallet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("config.salt")
public class Salt {
    private int length;
    private Boolean useLetters;
    private Boolean useNumbers;
}
