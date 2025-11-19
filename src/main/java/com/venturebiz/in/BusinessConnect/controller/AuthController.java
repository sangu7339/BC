package com.venturebiz.in.BusinessConnect.controller;

import com.venturebiz.in.BusinessConnect.dto.RegisterRequest;
import com.venturebiz.in.BusinessConnect.dto.SendOtpRequest;
import com.venturebiz.in.BusinessConnect.dto.VerifyOtpRequest;
import com.venturebiz.in.BusinessConnect.model.Role;
import com.venturebiz.in.BusinessConnect.model.User;
import com.venturebiz.in.BusinessConnect.repository.UserRepository;
import com.venturebiz.in.BusinessConnect.response.ApiResponse;
import com.venturebiz.in.BusinessConnect.response.ResponseBuilder;
import com.venturebiz.in.BusinessConnect.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

  
    private String generateReferralCode() {
        String code;
        do {
            code = "VBZ" + (int) (Math.random() * 90000 + 10000);  // Example: VBZ12345
        } while (userRepository.existsByReferralCode(code));
        return code;
    }

    // Register User (Business logic)
    private User handleRegistration(RegisterRequest request) {

        if (userRepository.existsByEmailAddress(request.getEmailAddress())) {
            throw new RuntimeException("Email already exists");
        }

        // Check referral code validity if provided
        if (request.getReferredBy() != null && !request.getReferredBy().isBlank()) {
            userRepository.findByReferralCode(request.getReferredBy())
                    .orElseThrow(() -> new RuntimeException("Invalid referral code"));
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmailAddress())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .referredBy(request.getReferredBy())
                .referralCode(generateReferralCode())
                .registeredAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    // Send OTP (Business logic)
    private void handleSendOtp(SendOtpRequest request) {

        User user = userRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        user.setOtp(otp);
        user.setOtpGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        System.out.println("OTP for " + user.getEmailAddress() + ": " + otp);
    }

    // Verify OTP (Business logic)
    private User handleVerifyOtp(VerifyOtpRequest request) {

        User user = userRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getOtp().equals(user.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        user.setOtp(null);
        userRepository.save(user);

        return user;
    }

    // ------------------------------------------------------------------
    // CONTROLLER ENDPOINTS USING INTERNAL SERVICE LOGIC
    // ------------------------------------------------------------------

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = handleRegistration(request);
            return ResponseEntity.ok(
                    ResponseBuilder.success("User registered successfully",
                            Map.of(
                                    "emailAddress", user.getEmailAddress(),
                                    "referralCode", user.getReferralCode(),
                                    "registeredAt", user.getRegisteredAt()
                            )
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseBuilder.error(e.getMessage()));
        }
    }

    // SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest request) {
        try {
            handleSendOtp(request);
            return ResponseEntity.ok(ResponseBuilder.success("OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseBuilder.error(e.getMessage()));
        }
    }

    // VERIFY OTP (LOGIN)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        try {
            User user = handleVerifyOtp(request);

            String token = jwtService.generateToken(
                    user.getEmailAddress(),
                    "ROLE_" + user.getRole().name()
            );

            return ResponseEntity.ok(
                    ResponseBuilder.success("Login successful",
                            Map.of(
                                    "token", token,
                                    "emailAddress", user.getEmailAddress(),
                                    "role", user.getRole().name(),
                                    "referralCode", user.getReferralCode()
                            )
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseBuilder.error(e.getMessage()));
        }
    }
}
