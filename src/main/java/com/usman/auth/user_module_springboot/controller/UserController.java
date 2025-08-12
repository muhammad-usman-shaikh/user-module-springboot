package com.usman.auth.user_module_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usman.auth.user_module_springboot.dto.ApiResponse;
import com.usman.auth.user_module_springboot.dto.RegisterUserDTO;
import com.usman.auth.user_module_springboot.dto.VerifyEmailRequestDTO;
import com.usman.auth.user_module_springboot.otp.OtpService;
import com.usman.auth.user_module_springboot.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final OtpService otpService;

    @Autowired
    public UserController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    /**
     * Register a new user account.
     *
     * @param payload User registration info
     * @return The newly created user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterUserDTO payload) {
        userService.registerUser(payload);
        ApiResponse<Object> response = new ApiResponse<>(true, "User registered successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Verify the OTP sent to the user during registration and mark the user as
     * email verified if the OTP is correct.
     *
     * @param request OTP to verify
     * @return 200 if the OTP is correct and the user was marked as email verified,
     *         400 if the OTP is invalid.
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@Valid @RequestBody VerifyEmailRequestDTO request) {
        boolean verified = otpService.verifyOtpAndActivateUser(request.getOtp());
        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<Object>(false, "Invalid OTP"));
        }
        ApiResponse<Object> response = new ApiResponse<>(true, "Email verified");
        return ResponseEntity.ok(response);
    }

}
