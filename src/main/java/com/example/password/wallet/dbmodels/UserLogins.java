package com.example.password.wallet.dbmodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name = "userLogins")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserLogins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ostatnia_data_logowania")
    private Timestamp lastLoginDate;

    @Column(name = "czy_poprawne_logowanie", columnDefinition = "boolean default false")
    private boolean isCorrectLogin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
