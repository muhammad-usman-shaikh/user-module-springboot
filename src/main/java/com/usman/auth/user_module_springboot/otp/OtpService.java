package com.usman.auth.user_module_springboot.otp;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.usman.auth.user_module_springboot.model.Otp;
import com.usman.auth.user_module_springboot.model.User;

@Service
public class OtpService {

    private final OtpRepository otpRepository;

    @Autowired
    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    /**
     * Generates a 6-digit OTP, saves it to DB with 10 min expiry, and returns the
     * OTP.
     */
    public String generateAndSaveOTP(User user) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        Otp otp = new Otp();
        otp.setUser(user);
        otp.setCode(otpCode);
        otpRepository.save(otp);
        return otpCode;
    }
}
