package com.usman.auth.user_module_springboot.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailRequestDTO {
    @NotBlank(message = "OTP is required")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
