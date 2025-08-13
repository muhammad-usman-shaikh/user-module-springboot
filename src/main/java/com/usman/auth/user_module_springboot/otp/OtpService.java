package com.usman.auth.user_module_springboot.otp;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usman.auth.user_module_springboot.user.User;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final long otpExpirySeconds;

    @Autowired
    public OtpService(
            OtpRepository otpRepository,
            @Value("${registration.otp.expiry.seconds}") long otpExpirySeconds) {
        this.otpRepository = otpRepository;
        this.otpExpirySeconds = otpExpirySeconds;
    }

    /**
     * Generates a random 6-digit OTP, saves it in the database linked to the
     * user and returns the OTP code.
     *
     * @param user the user to save the OTP for
     * @return the generated OTP code
     */
    public String generateAndSaveOTP(User user) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        Otp otp = new Otp();
        otp.setUser(user);
        otp.setCode(otpCode);
        otpRepository.save(otp);
        return otpCode;
    }

    /**
     * Verify the OTP and if correct, mark the user as email verified.
     *
     * @param otpCode the OTP to verify
     * @return true if the OTP is correct and the user was marked as verified,
     *         false otherwise.
     */
    @Transactional
    public boolean verifyOtpAndActivateUser(String otpCode) {
        Optional<Otp> otpOptional = otpRepository.findByCode(otpCode);

        if (otpOptional.isEmpty()) {
            return false;
        }

        Otp otp = otpOptional.get();
        User user = otp.getUser();

        // If already verified
        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            return false;
        }

        // Check expiry
        LocalDateTime expiryTime = otp.getCreatedAt().plusSeconds(otpExpirySeconds);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            return false;
        }

        // Mark user as verified
        user.setIsEmailVerified(true);
        otpRepository.deleteByUser(user); // remove OTPs for this user
        return true;
    }
}
