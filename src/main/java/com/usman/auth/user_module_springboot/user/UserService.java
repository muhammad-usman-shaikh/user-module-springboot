package com.usman.auth.user_module_springboot.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.usman.auth.user_module_springboot.dto.LoginDTO;
import com.usman.auth.user_module_springboot.dto.LoginResponseDTO;
import com.usman.auth.user_module_springboot.dto.RegisterUserDTO;
import com.usman.auth.user_module_springboot.email.EmailService;
import com.usman.auth.user_module_springboot.email.dto.EmailRequest;
import com.usman.auth.user_module_springboot.exception.InvalidCredentialsException;
import com.usman.auth.user_module_springboot.jwt.JwtService;
import com.usman.auth.user_module_springboot.otp.OtpService;
import com.usman.auth.user_module_springboot.session.Session;
import com.usman.auth.user_module_springboot.session.SessionService;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final SessionService sessionService;

    @Autowired
    public UserService(UserRepository userRepository,
            OtpService otpService,
            EmailService emailService,
            JwtService jwtService,
            SessionService sessionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.otpService = otpService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    /**
     * Register a new user and generate an OTP to verify the email.
     *
     * @param dto User registration info
     * @return The newly created user
     */
    public User registerUser(RegisterUserDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // hashed password
        User savedUser = userRepository.save(user);

        // Send email with OTP
        processEmailVerification(savedUser);

        return savedUser;
    }

    public void processEmailVerification(User user) {
        String otpCode = otpService.generateAndSaveOTP(user);

        // Send email asynchronously (non-blocking)
        emailService.sendEmail(new EmailRequest(
                user.getEmail(),
                "Your OTP Code",
                "Hello " + user.getFirstName() + ",\n\nYour OTP is: " + otpCode));
    }

    /**
     * Login a user and generate an access token and refresh token.
     *
     * @param payload User login info
     * @return The generated access token and refresh token
     * @throws InvalidCredentialsException if the user credentials are invalid
     */
    public LoginResponseDTO processLogin(LoginDTO payload) {
        Optional<User> user = verifyCredentials(payload.getEmail(), payload.getPassword());
        if (user.isEmpty()) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        if (user.get().getIsEmailVerified() == false) {
            processEmailVerification(user.get());
            throw new InvalidCredentialsException(
                    "Email not verified. Please check your email for the OTP.");
        }
        String refreshToken = jwtService.generateRefreshToken(
                user.get().getId(),
                user.get().getRole());
        Session session = sessionService.createSession(user.get(), refreshToken);
        String accessToken = jwtService.generateAccessToken(
                session.getId(),
                user.get().getId(),
                user.get().getRole());
        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setFirstName(user.get().getFirstName());
        response.setLastName(user.get().getLastName());
        response.setEmail(user.get().getEmail());
        response.setRole(user.get().getRole());
        response.setIsEmailVerified(user.get().getIsEmailVerified());
        response.setIs2faEnabled(user.get().getIs2faEnabled());
        return response;
    }

    public Optional<User> verifyCredentials(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        return passwordEncoder.matches(password, optionalUser.get().getPassword())
                ? optionalUser
                : Optional.empty();
    }

}
