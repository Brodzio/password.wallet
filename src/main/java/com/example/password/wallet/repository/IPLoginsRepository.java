package com.example.password.wallet.repository;

import com.example.password.wallet.dbmodels.IPLogins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPLoginsRepository extends JpaRepository<IPLogins, Long> {

    @Query(value = "select * from ip_logins where user_id = :id order by ostatnia_data_logowania DESC", nativeQuery = true)
    List<IPLogins> findAllIpLoginsByUserId(@Param("id") Long id);

    @Query(value = "select distinct ip_address from ip_logins where user_id = :id", nativeQuery = true)
    List<String> getIpListByUserId(@Param("id") Long id);
}
