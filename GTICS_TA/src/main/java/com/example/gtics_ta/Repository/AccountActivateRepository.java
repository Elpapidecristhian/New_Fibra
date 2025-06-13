package com.example.gtics_ta.Repository;

import com.example.gtics_ta.Entity.AccountActivate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountActivateRepository extends JpaRepository<AccountActivate, String> {
    AccountActivate findByToken(String token);
}
