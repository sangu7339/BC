package com.venturebiz.in.BusinessConnect.dto;

import lombok.Data;

/**
 * DTO to verify OTP and login.
 */
@Data
public class VerifyOtpRequest {
    private String emailAddress;
    private String otp;
}
