package com.usman.auth.user_module_springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.usman.auth.user_module_springboot.dto.RegisterUserDTO;
import com.usman.auth.user_module_springboot.email.EmailService;
import com.usman.auth.user_module_springboot.email.dto.EmailRequest;
import com.usman.auth.user_module_springboot.model.User;
import com.usman.auth.user_module_springboot.otp.OtpService;
import com.usman.auth.user_module_springboot.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository,
            OtpService otpService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public User registerUser(RegisterUserDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // hashed password
        User savedUser = userRepository.save(user);

        // Generate and store OTP
        String otpCode = otpService.generateAndSaveOTP(savedUser);

        // Send email asynchronously (non-blocking)
        emailService.sendEmail(new EmailRequest(
                savedUser.getEmail(),
                "Your OTP Code",
                "Hello " + savedUser.getFirstName() + ",\n\nYour OTP is: " + otpCode));

        return savedUser;
    }
}
