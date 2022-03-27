package com.example.password.wallet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("config")
@Data
public class EncodingConfiguration {
    private String pepper;
    private Salt salt;
}
