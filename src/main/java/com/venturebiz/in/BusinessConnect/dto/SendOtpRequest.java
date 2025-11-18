package com.venturebiz.in.BusinessConnect.dto;

import lombok.Data;

/**
 * DTO to request sending an OTP to a registered user.
 */
@Data
public class SendOtpRequest {
    private String emailAddress;
}
