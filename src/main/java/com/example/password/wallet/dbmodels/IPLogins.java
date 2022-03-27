package com.example.password.wallet.dbmodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name = "ipLogins")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class IPLogins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "ostatnia_data_logowania")
    private Timestamp lastLoginDate;

    @Column(name = "czy_poprawne_logowanie", columnDefinition = "boolean default false")
    private boolean isCorrectLogin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
