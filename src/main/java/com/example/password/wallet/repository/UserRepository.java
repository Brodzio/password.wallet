package com.example.password.wallet.repository;

import com.example.password.wallet.dbmodels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLogin(String login);

    User findByEmail(String email);

    User findByLogin(String login);

    @Modifying
    @Query(value = "update users set password_hash = :passwordHash, salt = :salt, is_password_kept_as_hash = :isPasswordKeptAsHash where id = :id ",  nativeQuery = true)
    void updateUserDataWithNewPassword(
            @Param("isPasswordKeptAsHash") Boolean isPasswordKeptAsHash,
            @Param("passwordHash") String passwordHash,
            @Param("salt") String salt,
            @Param("id") Long id);
}
