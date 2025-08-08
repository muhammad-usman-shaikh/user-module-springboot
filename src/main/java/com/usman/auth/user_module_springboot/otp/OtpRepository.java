package com.usman.auth.user_module_springboot.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import com.usman.auth.user_module_springboot.model.Otp;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
}