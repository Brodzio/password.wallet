package com.example.password.wallet.repository;

import com.example.password.wallet.dbmodels.UserLogins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLoginsRepository extends JpaRepository<UserLogins, Long> {

    @Query(value = "select * from user_logins where user_id = :id order by ostatnia_data_logowania DESC", nativeQuery = true)
    List<UserLogins> findAllByUser(@Param("id") Long id);

    @Query(value = "SELECT * FROM user_logins WHERE user_id = :id && czy_poprawne_logowanie = :correct order by ostatnia_data_logowania DESC LIMIT 1,1", nativeQuery = true)
    UserLogins findLastCorrectUserLoginByUserId(@Param("id") Long id, @Param("correct") Integer correct);

    @Query(value = "SELECT * FROM user_logins WHERE user_id = :id && czy_poprawne_logowanie = :wrong order by ostatnia_data_logowania DESC LIMIT 1", nativeQuery = true)
    UserLogins findLastWrongUserLoginByUserId(@Param("id") Long id, @Param("wrong") Integer wrong);
}
