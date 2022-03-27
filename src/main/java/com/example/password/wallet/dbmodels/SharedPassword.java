package com.example.password.wallet.dbmodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "shared_passwords")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SharedPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password")
    private String password;

    @Column(name = "web_address")
    private String webAddress;

    @Column(name = "login")
    private String login;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
