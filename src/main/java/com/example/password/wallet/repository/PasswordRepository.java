package com.example.password.wallet.repository;

import com.example.password.wallet.dbmodels.Password;
import com.example.password.wallet.dbmodels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

    @Query(value = "select * from passwords where user_id = :id", nativeQuery = true)
    List<Password> findAllByUser(@Param("id") Long id);

    @Modifying
    @Query(value = "update passwords set password = :password where id = :id", nativeQuery = true)
    void updatePasswordDataWithNewPassword(
            @Param("password") String password,
            @Param("id") Long id);

    @Modifying
    @Query(value = "update passwords set password = :password, login = :login, web_address = :webAddress where id = :id", nativeQuery = true)
    void updatePasswordFromWallet(
            @Param("id") Long id,
            @Param("login") String login,
            @Param("password") String password,
            @Param("webAddress") String webAddress
    );
}
