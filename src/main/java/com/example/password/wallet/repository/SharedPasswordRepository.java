package com.example.password.wallet.repository;

import com.example.password.wallet.dbmodels.SharedPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SharedPasswordRepository extends JpaRepository<SharedPassword, Long> {

    @Query(value = "select * from shared_passwords where user_id = :id", nativeQuery = true)
    public List<SharedPassword> findAllByUser(@Param("id") Long id);
}
