package com.usman.auth.user_module_springboot.otp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.usman.auth.user_module_springboot.model.Otp;
import com.usman.auth.user_module_springboot.model.User;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByCode(String code);

    @Transactional
    @Modifying
    void deleteByUser(User user);
}